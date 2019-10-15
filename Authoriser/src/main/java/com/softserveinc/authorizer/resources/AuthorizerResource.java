package com.softserveinc.authorizer.resources;

import com.softserveinc.authorizer.services.AuthorizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Service
@Path("/")
public class AuthorizerResource {
    private AuthorizerService authorizerService;

    @Autowired
    public AuthorizerResource(AuthorizerService authorizerService) {
        this.authorizerService = authorizerService;
    }

    @GET
    @Path("/verify/{verificationToken}")
    public void verifyEmail(@PathParam("verificationToken") String verificationToken){
        authorizerService.verifyEmail(verificationToken);
    }
}
