package com.softserveinc.feecharger.services.request_senders;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("requestSenderHttpService")
public class RequestSenderHttpService implements BookSenderService,LogSenderService,MailSenderService{
    private OktaService oktaService;
    private MainConfig mainConfig;

    @Autowired
    public RequestSenderHttpService(OktaService oktaService, MainConfig mainConfig) {
        this.oktaService = oktaService;
        this.mainConfig = mainConfig;
    }

    @Override
    public void sendEmail(Mail mail){
        OAuth2AccessToken accessToken = oktaService.getOktaToken();
        Client client = ClientBuilder.newClient();

        client.target(mainConfig.getMailSenderService().getUrl())
                .path("/mail")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken.getValue())
                .async()
                .post(Entity.entity(mail, MediaType.APPLICATION_JSON));
    }

    public void sendReturnBook(User user,Book book){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();
        Client client= ClientBuilder.newClient();

        client.target(mainConfig.getLibraryService().getUrl())
                .path("/users/"+user.getId()+"/books/"+book.getId())
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+accessToken.getValue())
                .async()
                .delete();
    }

    @Override
    public void sendPaymentLog(UserBookPaymentLog userBookPaymentLog){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();
        Client client=ClientBuilder.newClient();

        client.target(mainConfig.getLoggerService().getUrl())
                .path("/payments")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+accessToken.getValue())
                .async()
                .post(Entity.entity(userBookPaymentLog, MediaType.APPLICATION_JSON));
    }
}
