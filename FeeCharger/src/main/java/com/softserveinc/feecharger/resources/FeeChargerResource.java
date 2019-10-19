package com.softserveinc.feecharger.resources;

import com.softserveinc.feecharger.services.FeeChargerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Service
@Path("/")
public class FeeChargerResource {
    private FeeChargerService feeChargerService;

    @Autowired
    public FeeChargerResource(FeeChargerService feeChargerService) {
        this.feeChargerService = feeChargerService;
    }

    @PUT
    @Path("/users/{userId}/books/{bookId}")
    public void payForTakenBook(@PathParam("userId") Long userId,
                                @PathParam("bookId") Long bookId){
        feeChargerService.tryExtendRent(userId,bookId);
    }
}
