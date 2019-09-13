package com.resources;

import com.api.UserBookLog;
import com.dao.UserBookLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/actions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserBookLogResource {

    private UserBookLogDao userBookLogDao;

    @Autowired
    public UserBookLogResource(UserBookLogDao userBookLogDao) {
        this.userBookLogDao = userBookLogDao;
    }

    @GET
    public Response getLogs(@QueryParam("bookId") Long bookId,
                            @QueryParam("userId") String userId) {
        if (bookId != null && userId != null) {
            return Response.status(Response.Status.OK)
                    .entity(userBookLogDao.findByBookAndUser(userId,bookId))
                    .build();
        }
        if (bookId != null) {
            return Response.status(Response.Status.OK).
                    entity(userBookLogDao.findByBookId(bookId)).
                    build();
        }
        if (userId != null) {
            return Response.status(Response.Status.OK).
                    entity(userBookLogDao.findByUser(userId)).
                    build();
        }
        return Response.status(Response.Status.OK).
                entity(userBookLogDao.findAll()).
                build();
    }

    @POST
    public Response create(UserBookLog userBookLog) {
        userBookLogDao.save(userBookLog);
        return Response.status(Response.Status.OK).
                entity(userBookLog).
                build();
    }
}
