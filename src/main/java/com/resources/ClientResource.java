package com.resources;

import com.dao.ClientDao;
import com.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {
    private ClientDao clientDao;

    @Autowired
    public ClientResource(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

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
}
