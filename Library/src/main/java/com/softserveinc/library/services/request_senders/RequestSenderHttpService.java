package com.softserveinc.library.services.request_senders;

import com.softserveinc.cross_api_objects.models.Mail;
import com.softserveinc.library.MainConfig;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.services.OktaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

@Service("requestSenderHttpService")
public class RequestSenderHttpService implements RequestSenderService {
    private OktaService oktaService;
    private MainConfig mainConfig;
    private static final Logger log= LoggerFactory.getLogger(RequestSenderHttpService.class);

    @Autowired
    public RequestSenderHttpService(OktaService oktaService, MainConfig mainConfig) {
        this.oktaService = oktaService;
        this.mainConfig = mainConfig;
    }

    public void postUserBookLog(UserBookLog userBookLog){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();

        log.info("Sending request to Logger service. Sending logs.");

        Client client = ClientBuilder.newClient();
        client.target(mainConfig.getLoggerService().getUrl())
                .path("/actions")
                .request(MediaType.APPLICATION_JSON)
                .header(mainConfig.getSecurity().getTokenHeader(),mainConfig.getSecurity().getTokenPrefix()+accessToken.getValue())
                .async()
                .post(Entity.entity(userBookLog, MediaType.APPLICATION_JSON));
    }

    public void postChargeBookFee(Long userId,Long bookId){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();

        log.info("Sending request to FeeCharger service. Charge book's fee");

        Client client = ClientBuilder.newClient();
        client.target(mainConfig.getFeeChargerService().getUrl())
                .path("/users/"+userId+"/books/"+bookId)
                .request(MediaType.APPLICATION_JSON)
                .header(mainConfig.getSecurity().getTokenHeader(),mainConfig.getSecurity().getTokenPrefix()+accessToken.getValue())
                .async()
                .put(Entity.json(""));
    }

    public void sendEmailVerification(Mail mail){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();

        log.info("Sending request to Mail service. Verify email.");

        Client client=ClientBuilder.newClient();
        client.target(mainConfig.getMailSenderService().getUrl())
                .path("/mail")
                .request(MediaType.APPLICATION_JSON)
                .header(mainConfig.getSecurity().getTokenHeader(),mainConfig.getSecurity().getTokenPrefix()+accessToken.getValue())
                .async()
                .post(Entity.entity(mail,MediaType.APPLICATION_JSON));
    }
}
