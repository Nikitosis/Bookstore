package com.resources;

import com.models.Book;
import com.services.BookService;
import com.services.storage.StoredFile;
import io.swagger.annotations.Api;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

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

    @PUT
    @Path("/{bookId}/file")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response setBookFile(
            @PathParam("bookId") Long bookId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition){
        Book book=bookService.findById(bookId);

        if(book==null)
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();

        try {
            bookService.addFileToBook(book,new StoredFile(fileStream,fileDisposition.getFileName()));
            return Response.status(Response.Status.OK).entity(book).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong file").build();
        } catch(IllegalArgumentException e){
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Wrong file type").build();
        }

    }

    @PUT
    @Path("/{bookId}/image")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response setBookImage(
            @PathParam("bookId") Long bookId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition){
        Book book=bookService.findById(bookId);

        if(book==null)
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();

        try {
            bookService.addImageToBook(book,new StoredFile(fileStream,fileDisposition.getFileName()));
            return Response.status(Response.Status.OK).entity(book).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong file").build();
        } catch(IllegalArgumentException e){
            e.printStackTrace();
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Wrong file type").build();
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
