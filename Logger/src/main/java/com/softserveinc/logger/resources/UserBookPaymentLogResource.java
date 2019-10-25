package com.softserveinc.logger.resources;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.logger.dao.UserBookPaymentLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/payments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserBookPaymentLogResource {

    private UserBookPaymentLogDao userBookPaymentLogDao;

    @Autowired
    public UserBookPaymentLogResource(UserBookPaymentLogDao userBookPaymentLogDao) {
        this.userBookPaymentLogDao = userBookPaymentLogDao;
    }

    @GET
    public Response getPaymentLogs(@QueryParam("bookId") Long bookId,
                               @QueryParam("userId") Long userId){
        if (bookId != null && userId != null) {
            return Response.status(Response.Status.OK)
                    .entity(userBookPaymentLogDao.findByBookAndUser(userId,bookId))
                    .build();
        }
        if (bookId != null) {
            return Response.status(Response.Status.OK).
                    entity(userBookPaymentLogDao.findByBookId(bookId)).
                    build();
        }
        if (userId != null) {
            return Response.status(Response.Status.OK).
                    entity(userBookPaymentLogDao.findByUser(userId)).
                    build();
        }
        return Response.status(Response.Status.OK).
                entity(userBookPaymentLogDao.findAll()).
                build();
    }

    @POST
    public Response create(@Valid UserBookPaymentLog userBookPaymentLog) {
        userBookPaymentLogDao.save(userBookPaymentLog);
        return Response.status(Response.Status.OK).
                entity(userBookPaymentLog).
                build();
    }
}
