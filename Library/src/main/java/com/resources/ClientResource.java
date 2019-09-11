package com.resources;

import com.dao.BookDao;
import com.dao.ClientDao;
import com.models.Book;
import com.models.Client;
import com.services.BookService;
import com.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ClientService clientService;

    private BookService bookService;

    @Autowired
    public ClientResource(ClientService clientService, BookService bookService) {
        this.clientService = clientService;
        this.bookService = bookService;
    }

    @GET
    public Response getClients(){
        return Response.ok(clientService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getClientById(@PathParam("id") Long id){
        Client client=clientService.findById(id);
        if(client!=null){
            return Response.ok(client).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Client cannot be found").build();
        }
    }

    @POST
    public Response addClient(@Valid Client client){
        clientService.save(client);
        return Response.status(Response.Status.CREATED).entity(client).build();
    }

    @PUT
    public Response updateClient(@Valid Client client){
        if(clientService.findById(client.getId())!=null){
            clientService.update(client);
            return Response.ok(clientService.findById(client.getId())).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Client cannot be found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id){
        if(clientService.findById(id)!=null){
            clientService.delete(id);
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Client cannot be found").build();
        }
    }

    @GET
    @Path("/{clientId}/books")
    public Response getTakenClientBooks(@PathParam("clientId") Long clientId){
        if(clientService.findById(clientId)!=null){
            List<Book> takenBooks=bookService.findTakenByClientId(clientId);
            return Response.ok().entity(takenBooks).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Client cannot be found").build();
        }
    }

    @PUT
    @Path("/{clientId}/books")
    public Response takeBook(@PathParam("clientId") Long clientId,
                             @QueryParam("bookId") Long bookId){
        if(bookService.findById(bookId)!=null && clientService.findById(clientId)!=null){
            if(!bookService.isTaken(bookId)){
                bookService.takeBook(clientId,bookId);
                return Response.ok().build();
            }
            else{
                //if book is already taken
                return Response.status(Response.Status.BAD_REQUEST).entity("Book is already taken").build();
            }
        }
        else{
            //if book or client cannot be found
            return Response.status(Response.Status.NOT_FOUND).entity("Book or client cannot be found").build();
        }
    }

    @DELETE
    @Path("/{clientId}/books")
    public Response returnBook(@PathParam("clientId") Long clientId,
                               @QueryParam("bookId") Long bookId){
        if(bookService.findById(bookId)!=null && clientService.findById(clientId)!=null){
            if(bookService.isTakenByClient(clientId,bookId)){
                bookService.returnBook(clientId,bookId);
                return Response.ok().build();
            }
            else{
                //if book is not taken by this client
                return Response.status(Response.Status.BAD_REQUEST).entity("Book is not taken by this client").build();
            }
        }
        else{
            //if book or client cannot be found
            return Response.status(Response.Status.NOT_FOUND).entity("Book or client cannot be found").build();
        }
    }
}
