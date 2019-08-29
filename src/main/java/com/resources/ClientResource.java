package com.resources;

import com.dao.BookDao;
import com.dao.ClientDao;
import com.models.Book;
import com.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Service
@Path("/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {
    private ClientDao clientDao;

    private BookDao bookDao;

    public ClientResource(ClientDao clientDao, BookDao bookDao) {
        this.clientDao = clientDao;
        this.bookDao = bookDao;
    }

    @Autowired


    @GET
    public Response getClients(){
        return Response.ok(clientDao.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getClientById(@PathParam("id") Long id){
        Client client=clientDao.findById(id);
        if(clientDao.findById(id)!=null){
            return Response.ok(clientDao.findById(id)).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addClient(@Valid Client client){
        clientDao.save(client);
        return Response.status(Response.Status.CREATED).entity(client).build();
    }

    @PUT
    public Response updateClient(@Valid Client client){
        if(clientDao.findById(client.getId())!=null){
            clientDao.update(client);
            return Response.ok(client).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id){
        if(clientDao.findById(id)!=null){
            clientDao.delete(id);
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{clientId}/books")
    public Response getTakenClientBooks(@PathParam("clientId") Long clientId){
        if(clientDao.findById(clientId)!=null){
            List<Book> books=bookDao.findTakenByClientId(clientId);
            return Response.ok().entity(bookDao.findTakenByClientId(clientId)).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{clientId}/books")
    public Response takeBook(@PathParam("clientId") Long clientId,
                             @QueryParam("bookId") Long bookId){
        if(bookDao.findById(bookId)!=null && clientDao.findById(clientId)!=null){
            if(!bookDao.findById(bookId).isTaken()){
                bookDao.takeBook(clientId,bookId);
                return Response.ok().build();
            }
            else{
                //if book is already taken
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        else{
            //if book or client cannot be found
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{clientId}/books")
    public Response returnBook(@PathParam("clientId") Long clientId,
                               @QueryParam("bookId") Long bookId){
        if(bookDao.findById(bookId)!=null && clientDao.findById(clientId)!=null){
            if(bookDao.isTaken(clientId,bookId)){
                bookDao.returnBook(clientId,bookId);
                return Response.ok().build();
            }
            else{
                //if book is not taken by this client
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        else{
            //if book or client cannot be found
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
