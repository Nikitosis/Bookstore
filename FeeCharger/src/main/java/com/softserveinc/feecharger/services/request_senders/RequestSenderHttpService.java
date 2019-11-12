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
public class RequestSenderHttpService{
    private OktaService oktaService;
    private MainConfig mainConfig;

    @Autowired
    public RequestSenderHttpService(OktaService oktaService, MainConfig mainConfig) {
        this.oktaService = oktaService;
        this.mainConfig = mainConfig;
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
}
