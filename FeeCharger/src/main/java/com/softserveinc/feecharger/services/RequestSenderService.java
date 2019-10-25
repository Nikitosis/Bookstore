package com.softserveinc.feecharger.services;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.services.OktaService;
import com.softserveinc.feecharger.MainConfig;
import com.softserveinc.feecharger.dao.BookDao;
import com.softserveinc.feecharger.dao.UserDao;
import com.softserveinc.feecharger.models.UserBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.Future;

@Service
public class RequestSenderService {

    private UserDao userDao;
    private BookDao bookDao;
    private OktaService oktaService;
    private MainConfig mainConfig;

    @Autowired
    public RequestSenderService(UserDao userDao, BookDao bookDao, OktaService oktaService, MainConfig mainConfig) {
        this.userDao = userDao;
        this.bookDao = bookDao;
        this.oktaService = oktaService;
        this.mainConfig = mainConfig;
    }

    public Future<Response> sendReturnBookNotification(UserBook rent){
        User curUser=userDao.findById(rent.getUserId());
        Book book=bookDao.findById(rent.getBookId());

        if(curUser==null || curUser.getEmail()==null || !curUser.getEmailVerified())
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

    public Future<Response> sendReturnBook(UserBook rent){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();
        Client client= ClientBuilder.newClient();

        return client.target(mainConfig.getLibraryService().getUrl())
                .path("/users/"+rent.getUserId()+"/books/"+rent.getBookId())
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+accessToken.getValue())
                .async()
                .delete();
    }

    public Future<Response> sendPaymentLog(UserBook rent, BigDecimal payment, LocalDateTime dateTime){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();
        Client client=ClientBuilder.newClient();

        UserBookPaymentLog userBookPaymentLog=new UserBookPaymentLog();
        userBookPaymentLog.setBookId(rent.getBookId());
        userBookPaymentLog.setUserId(rent.getUserId());
        userBookPaymentLog.setPayment(payment);
        userBookPaymentLog.setDate(dateTime);

        return client.target(mainConfig.getLoggerService().getUrl())
                .path("/payments")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+accessToken.getValue())
                .async()
                .post(Entity.entity(userBookPaymentLog, MediaType.APPLICATION_JSON));
    }
}
