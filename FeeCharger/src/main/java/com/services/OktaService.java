package com.services;

import com.MainConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Service
public class OktaService {

    private MainConfig mainConfig;

    private OAuth2AccessToken curToken=null;

    @Autowired
    public OktaService(MainConfig mainConfig) {
        this.mainConfig = mainConfig;
    }

    private Form getForm(){
        Form formValues=new Form();
        formValues.param("grant_type","client_credentials");
        String scopes="";
        for(String scope:mainConfig.getOktaOAuth().getScopes()){
            scopes=scopes+scope+" ";
        }
        formValues.param("scope",scopes);
        return formValues;
    }

    private OAuth2AccessToken getOktaTokenFromRequest(){
        Form formValues=getForm();

        Client client = ClientBuilder.newBuilder()
                .register(HttpAuthenticationFeature.basic(
                        mainConfig.getOktaOAuth().getClientId(),
                        mainConfig.getOktaOAuth().getClientSecret()))
                .build();
        Response response=client.target(mainConfig.getOktaOAuth().getTokenPath())
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(formValues, MediaType.APPLICATION_FORM_URLENCODED));

        String json=response.readEntity(String.class);

        try {
            return new ObjectMapper().readValue(json, DefaultOAuth2AccessToken.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OAuth2AccessToken getOktaToken(){
        if(curToken==null || curToken.isExpired()){
            curToken=getOktaTokenFromRequest();
        }
        return curToken;
    }
}
