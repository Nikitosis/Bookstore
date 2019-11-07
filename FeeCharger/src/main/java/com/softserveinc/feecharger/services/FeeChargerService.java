package com.softserveinc.feecharger.services;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.feecharger.MainConfig;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.feecharger.dao.BookDao;
import com.softserveinc.feecharger.dao.FeeChargerDao;
import com.softserveinc.feecharger.dao.UserDao;
import com.softserveinc.feecharger.models.UserBook;
import com.softserveinc.feecharger.services.request_senders.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class FeeChargerService {
    private final static Logger log=LoggerFactory.getLogger(FeeChargerService.class);

    private BookDao bookDao;
    private FeeChargerDao feeChargerDao;
    private UserDao userDao;
    private MainConfig mainConfig;
    private BookSenderService bookSenderService;
    private LogSenderService logSenderService;
    private MailSenderService mailSenderService;
    private InvoiceService invoiceService;
    private Timer timer;

    @Autowired
    public FeeChargerService(BookDao bookDao, FeeChargerDao feeChargerDao, UserDao userDao, MainConfig mainConfig, RequestSenderHttpService bookSenderService, RequestSenderKafkaService logSenderService, RequestSenderKafkaService mailSenderService,InvoiceService invoiceService) {
        this.bookDao = bookDao;
        this.feeChargerDao = feeChargerDao;
        this.userDao = userDao;
        this.mainConfig = mainConfig;
        this.bookSenderService = bookSenderService;
        this.logSenderService = logSenderService;
        this.mailSenderService = mailSenderService;
        this.invoiceService=invoiceService;
    }

    //starting timer that will extend expired book rents every n minutes
    @PostConstruct
    private void startFeeCharging(){
        timer=new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        log.info("Timer fired.");
                        tryExtendRents(feeChargerDao.getExpiredBookRent());
                    }
                },
                0,
                mainConfig.getFeeChargeConfig().getCheckExpirationInterval() * 1000 * 60
        );
    }

    public void tryExtendRents(List<UserBook> rents){
        for(UserBook expiredRent:rents){
            tryExtendRent(expiredRent);
        }
    }

    //function that decides,whether user have enough money to extend rent or just return book
    public synchronized void tryExtendRent(UserBook rent){
        //if rent is still valid. No need to extend it
        if(rent.getPaidUntil()!=null && rent.getPaidUntil().compareTo(LocalDateTime.now())>=0){
            return;
        }

        if(canUserPayForRent(rent)){
            log.info("Extending book rent.UserId: "+rent.getUserId()+". BookId: "+rent.getBookId());
            extendBookRent(rent);
        }
        else{
            log.info("Sending return book.UserId: "+rent.getUserId()+". BookId: "+rent.getBookId());
            mailSenderService.sendEmail(createReturnMailNotification(
                    userDao.findById(rent.getUserId()),
                    bookDao.findById(rent.getBookId())
            ));
            bookSenderService.sendReturnBook(
                    userDao.findById(rent.getUserId()),
                    bookDao.findById(rent.getBookId())
            );
        }
    }

    public void tryExtendRent(Long userId, Long bookId){
        UserBook curRent=feeChargerDao.getBookRent(userId,bookId);
        if(curRent==null)
            return;

        tryExtendRent(curRent);
    }

    private boolean canUserPayForRent(UserBook rent){
        User user=userDao.findById(rent.getUserId());
        Book book=bookDao.findById(rent.getBookId());

        return user.getMoney().compareTo(book.getPrice())>=0;
    }

    private void extendBookRent(UserBook rent){
        Book book=bookDao.findById(rent.getBookId());
        feeChargerDao.chargeFee(rent.getUserId(),book.getPrice());

        UserBookPaymentLog userBookPaymentLog=createUserBookPaymentLog(rent,book.getPrice(),LocalDateTime.now());
        logSenderService.sendPaymentLog(userBookPaymentLog);

        LocalDateTime paidUntil=rent.getPaidUntil()==null ? LocalDateTime.now() : rent.getPaidUntil();
        LocalDateTime payUntil=paidUntil.plusMinutes(mainConfig.getFeeChargeConfig().getRentPeriod());
        feeChargerDao.extendBookRent(rent.getUserId(),rent.getBookId(), payUntil);

        String fileUrl=invoiceService.createInvoiceFile(
                userDao.findById(rent.getUserId()),
                bookDao.findById(rent.getBookId()),
                book.getPrice(),
                LocalDateTime.now()
        );
        System.out.println(fileUrl);
    }

    private Mail createReturnMailNotification(User user,Book book){
        return new Mail(
                user.getEmail(),
                "Cannot extend book's rent",
                "Unfortunately, you don't have enough money on your account to extend " +
                        book.getName() + " rent. Book cost is " + book.getPrice() +
                        " but your current balance is " + user.getMoney()
        );
    }

    private UserBookPaymentLog createUserBookPaymentLog(UserBook rent, BigDecimal price,LocalDateTime dateTime){
        UserBookPaymentLog userBookPaymentLog=new UserBookPaymentLog();
        userBookPaymentLog.setBookId(rent.getBookId());
        userBookPaymentLog.setUserId(rent.getUserId());
        userBookPaymentLog.setPayment(price);
        userBookPaymentLog.setDate(LocalDateTime.now());
        return userBookPaymentLog;
    }
}
