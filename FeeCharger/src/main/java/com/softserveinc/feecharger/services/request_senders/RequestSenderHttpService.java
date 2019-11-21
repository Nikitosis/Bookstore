package com.softserveinc.feecharger.services.request_senders;

import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.services.OktaService;
import com.softserveinc.cross_api_objects.utils.correlation_id.HttpClientCorrelationFilter;
import com.softserveinc.feecharger.MainConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Service("requestSenderHttpService")
public class RequestSenderHttpService{
    private static final Logger log= LoggerFactory.getLogger(RequestSenderHttpService.class);

    private OktaService oktaService;
    private MainConfig mainConfig;

    @Autowired
    public RequestSenderHttpService(OktaService oktaService, MainConfig mainConfig) {
        this.oktaService = oktaService;
        this.mainConfig = mainConfig;
    }

    public void sendReturnBook(User user,Book book){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();
        Client client= ClientBuilder.newClient().register(HttpClientCorrelationFilter.class);

        log.info("Sending return book. User id: "+user.getId()+". Book id: "+book.getId());

        client.target(mainConfig.getLibraryService().getUrl())
                .path("/users/"+user.getId()+"/books/"+book.getId())
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization","Bearer "+accessToken.getValue())
                .async()
                .delete();
    }
}
