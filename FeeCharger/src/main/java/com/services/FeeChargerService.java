package com.services;

import com.MainConfig;
import com.crossapi.models.Book;
import com.crossapi.models.Mail;
import com.crossapi.models.User;
import com.crossapi.services.OktaService;
import com.dao.BookDao;
import com.dao.FeeChargerDao;
import com.dao.UserDao;
import com.models.UserBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

@Service
public class FeeChargerService {
    private final static Logger log=LoggerFactory.getLogger(FeeChargerService.class);

    private BookDao bookDao;
    private FeeChargerDao feeChargerDao;
    private UserDao userDao;
    private MainConfig mainConfig;
    private OktaService oktaService;
    private Timer timer;

    @Autowired
    public FeeChargerService(BookDao bookDao, FeeChargerDao feeChargerDao, UserDao userDao, MainConfig mainConfig, OktaService oktaService) {
        this.bookDao = bookDao;
        this.feeChargerDao = feeChargerDao;
        this.userDao = userDao;
        this.mainConfig = mainConfig;
        this.oktaService = oktaService;
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
            sendReturnBookNotification(rent);
            sendReturnBook(rent);
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

        LocalDateTime paidUntil=rent.getPaidUntil()==null ? LocalDateTime.now() : rent.getPaidUntil();
        LocalDateTime payUntil=paidUntil.plusMinutes(mainConfig.getFeeChargeConfig().getRentPeriod());
        feeChargerDao.extendBookRent(rent.getUserId(),rent.getBookId(), payUntil);
    }

    private Future<Response> sendReturnBookNotification(UserBook rent){
        User curUser=userDao.findById(rent.getUserId());
        Book book=bookDao.findById(rent.getBookId());

        if(curUser==null || curUser.getEmail()==null)
            return null;

        OAuth2AccessToken accessToken = oktaService.getOktaToken();
        Client client = ClientBuilder.newClient();

        Mail mail = new Mail(
                curUser.getEmail(),
                "Cannot extend book's rent",
                "Unfortunately, you don't have enough money on your account to extend " +
                        book.getName() + " rent. Book cost is " + book.getPrice() +
                        " but your current balance is " + curUser.getMoney()
        );

        return client.target(mainConfig.getMailSenderService().getUrl())
                .path("/mail")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .async()
                .post(Entity.entity(mail, MediaType.APPLICATION_JSON));
    }

    private Future<Response> sendReturnBook(UserBook rent){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();
        Client client= ClientBuilder.newClient();

        return client.target(mainConfig.getLibraryService().getUrl())
                .path("/users/"+rent.getUserId()+"/books/"+rent.getBookId())
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+accessToken.getValue())
                .async()
                .delete();
    }
}
