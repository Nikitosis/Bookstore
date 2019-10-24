package com.softserveinc.authorizer.resources;

import com.softserveinc.authorizer.services.AuthorizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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
    public Response verifyEmail(@PathParam("verificationToken") String verificationToken){
        try {
            authorizerService.verifyEmail(verificationToken);
            return Response.ok().build();
        }
        catch(UsernameNotFoundException e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
