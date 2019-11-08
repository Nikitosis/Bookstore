package com.softserveinc.invoicer.services;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.models.Attachment;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.services.storage.StoredFile;
import com.softserveinc.invoicer.MainConfig;
import com.softserveinc.invoicer.dao.BookDao;
import com.softserveinc.invoicer.dao.UserDao;
import com.softserveinc.invoicer.dao.UserPaymentsDao;
import com.softserveinc.invoicer.models.UserPayments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class InvoiceService {
    private final static Logger log=LoggerFactory.getLogger(InvoiceService.class);

    private MainConfig mainConfig;
    private InvoiceAwsSaverService invoiceAwsSaverService;
    private InvoiceGeneratorService invoiceGeneratorService;
    private UserPaymentsDao userPaymentsDao;
    private Timer timer;

    @Autowired
    public InvoiceService(MainConfig mainConfig, InvoiceAwsSaverService invoiceAwsSaverService, InvoiceGeneratorService invoiceGeneratorService, UserPaymentsDao userPaymentsDao) {
        this.mainConfig = mainConfig;
        this.invoiceAwsSaverService = invoiceAwsSaverService;
        this.invoiceGeneratorService = invoiceGeneratorService;
        this.userPaymentsDao = userPaymentsDao;
    }

    @PostConstruct
    private void startInvoiceService(){
        timer=new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        log.info("Timer fired");
                        generateInvoices();
                    }
                },
                0,
                mainConfig.getInvoicerConfig().getGenerateInvoicePeriod()*1000*60
        );
    }

    public void generateInvoices(){
        List<UserPayments> paymentsByUser=userPaymentsDao.findUserPaymentsFromDate(
                LocalDateTime.now().minusMinutes(mainConfig.getInvoicerConfig().getGenerateInvoicePeriod())
        );

        for(UserPayments userPayments:paymentsByUser){
            StoredFile invoiceFile=invoiceGeneratorService.createInvoiceFile(userPayments);
            String fileUrl=invoiceAwsSaverService.saveInvoice(invoiceFile);
            System.out.println(fileUrl);
        }
    }

    private Mail createMailInvoice(User user, String fileUrl){
        Mail mail=new Mail();
        mail.setReceiverEmail(user.getEmail());
        mail.setSubject("Bookstore invoice");
        mail.setBody("Invoice is attached");
        mail.setAttachment(new Attachment("Invoice.pdf",fileUrl));
        return mail;
    }
}
