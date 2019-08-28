package com.resources;

import com.dao.ClientDao;
import com.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

    @Autowired
    private ClientDao clientDao;

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
    public Response addClient(Client client){
        clientDao.save(client);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    public Response updateClient(Client client){
        if(clientDao.findById(client.getId())!=null){
            clientDao.update(client);
            return Response.ok().build();
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
