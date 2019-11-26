package com.softserveinc.authorizer.resources;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.models.OAuth2AccessTokenResponse;
import com.softserveinc.authorizer.models.OAuth2UserInfo;
import com.softserveinc.authorizer.services.AuthorizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response handleAccessCode(@QueryParam("code") String code){
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

       // OAuth2AccessTokenResponse accessTokenResponse=response.readEntity(OAuth2AccessTokenResponse.class);
        OAuth2AccessTokenResponse accessTokenResponseEntity=accessTokenResponse.readEntity(OAuth2AccessTokenResponse.class);

        //get user info
        Response userInfoResponse =ClientBuilder.newClient()
                .target(mainConfig.getGoogleOauth().getUserInfoUri())
                .queryParam("access_token",accessTokenResponseEntity.getAccessToken())
                .request()
                .get();

        //String str=userInfoResponse.readEntity(String.class);
        OAuth2UserInfo oAuth2UserInfoEntity=userInfoResponse.readEntity(OAuth2UserInfo.class);

        return Response.ok().entity(oAuth2UserInfoEntity).build();
    }
}
