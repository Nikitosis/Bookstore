package com.softserveinc.authorizer.resources;

import com.softserveinc.authorizer.MainConfig;
import com.softserveinc.authorizer.services.AuthorizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
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
}
