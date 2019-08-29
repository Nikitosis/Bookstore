package com.resources;

import com.dao.BookDao;
import com.dao.ClientDao;
import com.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Path("/books")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {

    private BookDao bookDao;

    @Autowired
    public BookResource(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @GET
    public Response getBooks(){
        return Response.ok(bookDao.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id){
        Book book=bookDao.findById(id);
        if(bookDao.findById(id)!=null){
            return Response.ok(bookDao.findById(id)).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response addBook(@Valid Book book){
        bookDao.save(book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @PUT
    public Response updateBook(@Valid Book book){
        if(bookDao.findById(book.getId())!=null){
            bookDao.update(book);
            return Response.ok(book).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id){
        if(bookDao.findById(id)!=null){
            bookDao.delete(id);
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
