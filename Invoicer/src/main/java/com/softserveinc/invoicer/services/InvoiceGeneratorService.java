package com.softserveinc.invoicer.services;

import com.amazonaws.util.Base64;
import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.InvoiceData;
import com.softserveinc.cross_api_objects.models.InvoiceSinglePaymentData;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.services.storage.AwsStorageService;
import com.softserveinc.cross_api_objects.services.storage.StoredFile;
import com.softserveinc.invoicer.MainConfig;
import com.softserveinc.invoicer.dao.BookDao;
import com.softserveinc.invoicer.dao.UserDao;
import com.softserveinc.invoicer.models.UserPayments;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceGeneratorService {
    private MainConfig mainConfig;
    private AwsStorageService awsStorageService;
    private UserDao userDao;
    private BookDao bookDao;

    public InvoiceGeneratorService(MainConfig mainConfig, AwsStorageService awsStorageService, UserDao userDao, BookDao bookDao) {
        this.mainConfig = mainConfig;
        this.awsStorageService = awsStorageService;
        this.userDao = userDao;
        this.bookDao = bookDao;
    }

    public StoredFile createInvoiceFile(UserPayments userPayments){
        StoredFile storedFile=new StoredFile();
        storedFile.setFileName("Invoice.pdf");
        InvoiceData invoiceData=buildInvoiceData(userPayments);
        storedFile.setInputStream(new ByteArrayInputStream(getPdfBytes(invoiceData)));

        return storedFile;
    }

    private InvoiceData buildInvoiceData(UserPayments userPayments){
        List<InvoiceSinglePaymentData> singlePayments=new ArrayList<>();
        for(UserBookPaymentLog paymentLog:userPayments.getPaymentLogs()){
            Book book=bookDao.findById(paymentLog.getBookId());
            InvoiceSinglePaymentData singlePayment=new InvoiceSinglePaymentData();
            singlePayment.setBookName(book.getName());
            singlePayment.setDateTime(paymentLog.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            singlePayment.setPayment(paymentLog.getPayment().toString());

            singlePayments.add(singlePayment);
        }

        User user=userDao.findById(userPayments.getUserId());

        InvoiceData invoiceData=new InvoiceData();
        invoiceData.setUserName(user.getfName());
        invoiceData.setUserSurname(user.getlName());
        invoiceData.setInvoiceSinglePaymentData(singlePayments);

        return invoiceData;
    }

    private byte[] getPdfBytes(InvoiceData invoiceData){
        Client client = ClientBuilder.newClient();

        String response=client.target("https://qerhbbqfd2.execute-api.us-east-2.amazonaws.com/default/InvoicePDFGenerator")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(invoiceData,MediaType.APPLICATION_JSON),String.class);

        JSONObject jsonObject=(JSONObject)JSONValue.parse(response);

        byte[] decoded=Base64.decode(jsonObject.get("body").toString().getBytes());

        return decoded;
    }
}
