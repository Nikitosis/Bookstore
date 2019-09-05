package com;

import com.dao.ClientBookLogDao;
import com.models.ClientBookLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/actions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientBookLogResource {

    private ClientBookLogDao clientBookLogDao;

    @Autowired
    public ClientBookLogResource(ClientBookLogDao clientBookLogDao) {
        this.clientBookLogDao = clientBookLogDao;
    }

    @GET
    public Response getLogs(@QueryParam("bookId") Long bookId,
                            @QueryParam("clientId") Long clientId) {
        if (bookId != null && clientId != null) {
            return Response.status(Response.Status.OK)
                    .entity(clientBookLogDao.findByBookAndClient(clientId,bookId))
                    .build();
        }
        if (bookId != null) {
            return Response.status(Response.Status.OK).
                    entity(clientBookLogDao.findByBookId(bookId)).
                    build();
        }
        if (clientId != null) {
            return Response.status(Response.Status.OK).
                    entity(clientBookLogDao.findByClientId(clientId)).
                    build();
        }
        return Response.status(Response.Status.OK).
                entity(clientBookLogDao.findAll()).
                build();
    }

    @POST
    public Response create(ClientBookLog clientBookLog) {
        clientBookLogDao.save(clientBookLog);
        return Response.status(Response.Status.OK).
                entity(clientBookLog).
                build();
    }
}
