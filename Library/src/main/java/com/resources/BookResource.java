package com.resources;

import com.models.Book;
import com.services.BookService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Service
@Api(value="/books")
@Path("/books")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {

    private BookService bookService;

    @Autowired
    public BookResource(BookService bookService) {
        this.bookService = bookService;
    }

    @GET
    public Response getBooks(){
        return Response.ok(bookService.findAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id){
        Book book=bookService.findById(id);
        if(book!=null){
            return Response.ok(book).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }
    }

    @POST
    public Response addBook(@NotNull @Valid Book book){
        bookService.save(book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    }

    @PUT
    public Response updateBook(@NotNull @Valid Book book){
        if(bookService.findById(book.getId())!=null){
            bookService.update(book);
            return Response.ok(bookService.findById(book.getId())).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Long id){
        if(bookService.findById(id)!=null){
            bookService.delete(id);
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }
    }
}
