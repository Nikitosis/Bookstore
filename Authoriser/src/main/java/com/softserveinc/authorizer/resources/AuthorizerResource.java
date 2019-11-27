package com.softserveinc.authorizer.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.models.OAuth2AccessTokenResponse;
import com.softserveinc.authorizer.models.OAuth2UserInfo;
import com.softserveinc.authorizer.services.AuthorizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Service
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorizerResource {
    private AuthorizerService authorizerService;

   @Autowired
   private MainConfig mainConfig;

    @Autowired
    public AuthorizerResource(AuthorizerService authorizerService) {
        this.authorizerService = authorizerService;
    }

    @GET
    @Path("/verify/{verificationToken}")
    public Response verifyEmail(@PathParam("verificationToken") String verificationToken){
        try {
            authorizerService.verifyEmail(verificationToken);
            return Response.ok().build();
        }
        catch(UsernameNotFoundException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/login/oauth2/google/authorize-user")
    public Response getAuthorizationLink(){
        return Response.ok().entity(mainConfig.getGoogleOauth().getAuthorisationUri()+
                "?scope="+mainConfig.getGoogleOauth().getScope()+
                "&response_type=code"+
                "&access_type=offline"+
                "&redirect_uri="+mainConfig.getGoogleOauth().getRedirectUri()+
                "&client_id="+mainConfig.getGoogleOauth().getClientId()).build();
    }

    @GET
    @Path("/login/oauth2/code/google")
    public Response handleAccessCode(@QueryParam("code") String code) throws IOException {
        //get access token
        Form form = new Form();
        form
                .param("code", code)
                .param("client_id", mainConfig.getGoogleOauth().getClientId())
                .param("client_secret", mainConfig.getGoogleOauth().getClientSecret())
                .param("grant_type","authorization_code")
                .param("redirect_uri",mainConfig.getGoogleOauth().getRedirectUri());

        Response accessTokenResponse= ClientBuilder.newClient()
                .target(mainConfig.getGoogleOauth().getTokenUri())
                .request(MediaType.APPLICATION_FORM_URLENCODED)
                .post(Entity.form(form));

        String accessTokenResponseStr=accessTokenResponse.readEntity(String.class);
        OAuth2AccessTokenResponse accessTokenResponseEntity=new ObjectMapper().readValue(accessTokenResponseStr,OAuth2AccessTokenResponse.class);

        //get user info
        Response userInfoResponse =ClientBuilder.newClient()
                .target(mainConfig.getGoogleOauth().getUserInfoUri())
                .queryParam("access_token",accessTokenResponseEntity.getAccessToken())
                .request()
                .get();

        String userInfoResponseStr=userInfoResponse.readEntity(String.class);
        OAuth2UserInfo oAuth2UserInfoEntity=new ObjectMapper().readValue(userInfoResponseStr,OAuth2UserInfo.class);

        return Response.ok().entity(oAuth2UserInfoEntity)
                .build();
    }

    @GET
    @Path("/loginSuccess")
    public Response loginSuccess(OAuth2AuthenticationToken authentication){
        return Response.ok().build();
    }
}
