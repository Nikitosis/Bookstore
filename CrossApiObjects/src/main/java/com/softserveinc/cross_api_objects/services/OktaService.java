package com.softserveinc.cross_api_objects.services;

import com.softserveinc.cross_api_objects.configuration.OktaOAuthConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class OktaService {

    private OktaOAuthConfig oktaOAuthConfig;

    private OAuth2AccessToken curToken=null;

    public OktaService(OktaOAuthConfig oktaOAuthConfig) {
        this.oktaOAuthConfig = oktaOAuthConfig;
    }

    private Form getForm(){
        Form formValues=new Form();
        formValues.param("grant_type","client_credentials");
        String scopes="";
        for(String scope:oktaOAuthConfig.getScopes()){
            scopes=scopes+scope+" ";
        }
        formValues.param("scope",scopes);
        return formValues;
    }

    private OAuth2AccessToken getOktaTokenFromRequest(){
        Form formValues=getForm();

        Client client = ClientBuilder.newBuilder()
                .register(HttpAuthenticationFeature.basic(
                        oktaOAuthConfig.getClientId(),
                        oktaOAuthConfig.getClientSecret()))
                .build();
        Response response=client.target(oktaOAuthConfig.getTokenPath())
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
