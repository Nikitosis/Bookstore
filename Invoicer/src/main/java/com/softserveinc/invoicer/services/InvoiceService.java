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
    private InvoiceSenderService invoiceSenderService;
    private UserPaymentsDao userPaymentsDao;
    private UserDao userDao;
    private Timer timer;

    @Autowired
    public InvoiceService(MainConfig mainConfig, InvoiceAwsSaverService invoiceAwsSaverService, InvoiceGeneratorService invoiceGeneratorService, InvoiceSenderService invoiceSenderService, UserPaymentsDao userPaymentsDao, UserDao userDao) {
        this.mainConfig = mainConfig;
        this.invoiceAwsSaverService = invoiceAwsSaverService;
        this.invoiceGeneratorService = invoiceGeneratorService;
        this.invoiceSenderService = invoiceSenderService;
        this.userPaymentsDao = userPaymentsDao;
        this.userDao = userDao;
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
            invoiceSenderService.sendInvoice(
                    userDao.findById(userPayments.getUserId()),
                    fileUrl
            );
            System.out.println(fileUrl);
        }
    }
}
