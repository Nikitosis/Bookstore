package com.resources;

import com.amazonaws.services.codecommit.model.FileTooLargeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.Book;
import com.services.BookService;
import com.services.storage.StoredFile;
import com.utils.ObjectValidator;
import io.swagger.annotations.Api;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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

    private static final Logger log= LoggerFactory.getLogger(BookResource.class);

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
            log.warn("Book cannot be found. BookId: "+id);
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }
    }

    @PUT
    @Path("/{bookId}/file")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response setBookFile(
            @PathParam("bookId") Long bookId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            HttpServletRequest request){
        Book book=bookService.findById(bookId);

        if(book==null) {
            log.warn("Book cannot be found. BookId: " + bookId);
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }

        try {
            bookService.addFileToBook(book,new StoredFile(fileStream,fileDisposition.getFileName()),request.getContentLengthLong());
            return Response.status(Response.Status.OK).entity(book).build();
        } catch (IOException e) {
            log.warn("Cannot read the file",e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot read the file").build();
        } catch(IllegalArgumentException e){
            log.warn("Wrong file type",e.getMessage());
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Wrong file type").build();
        } catch(FileTooLargeException e){
            log.warn("Wrong file size",e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong file size").build();
        }
    }

    @PUT
    @Path("/{bookId}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response setBookImage(
            @PathParam("bookId") Long bookId,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDisposition,
            @Context final HttpServletRequest request){
        Book book=bookService.findById(bookId);

        if(book==null) {
            log.warn("Book cannot be found. BookId: "+bookId);
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }

        try {
            bookService.addImageToBook(book,new StoredFile(fileStream,fileDisposition.getFileName()),request.getContentLengthLong());
            return Response.status(Response.Status.OK).entity(book).build();
        } catch (IOException e) {
            log.warn("Cannot read the file");
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot read the file").build();
        } catch(IllegalArgumentException e){
            log.warn("Wrong file type",e.getMessage());
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity("Wrong file type").build();
        } catch(FileTooLargeException e){
            log.warn("Wrong file size",e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong file size").build();
        }
    }

    //here book's file and image are not mandatory.
    //The only requirement is valid bookInfo
    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response addBook(@FormDataParam("file") InputStream fileStream,
                         @FormDataParam("file") FormDataContentDisposition fileDisposition,
                         @FormDataParam("image") InputStream imageStream,
                         @FormDataParam("image") FormDataContentDisposition imageDisposition,
                         @FormDataParam("bookInfo") FormDataBodyPart bookPart,
                         @Context final HttpServletRequest request){
        if(bookPart==null){
            log.warn("bookInfo is empty");
            return Response.status(Response.Status.BAD_REQUEST).entity("bookInfo is empty").build();
        }
        //converting bookInfo json to book pojo
        bookPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        Book book=bookPart.getValueAs(Book.class);

        try{
            //validating the book
            ObjectValidator.validateFields(book);
        }catch(ValidationException e){
            log.warn("Invalid bookInfo");
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422).entity("Invalid bookInfo").build();
        }

        bookService.save(book);

        //adding file to book
        try {
            if(fileStream!=null)
                bookService.addFileToBook(book,new StoredFile(fileStream,fileDisposition.getFileName()),request.getContentLengthLong());
        }  catch (IOException e) {
            log.warn("Cannot read the file");
        } catch(IllegalArgumentException e){
            log.warn(e.getMessage());
        } catch(FileTooLargeException e){
            log.warn(e.getMessage());
        }

        //adding image to book
        try {
            if(imageStream!=null)
                bookService.addImageToBook(book,new StoredFile(imageStream,imageDisposition.getFileName()),request.getContentLengthLong());
        }  catch (IOException e) {
            log.warn("Cannot read the image");
        } catch(IllegalArgumentException e){
            log.warn(e.getMessage());
        } catch(FileTooLargeException e){
            log.warn(e.getMessage());
        }

        return Response.status(Response.Status.OK).entity(book).build();
    }

    @PUT
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response updateBook(@FormDataParam("file") InputStream fileStream,
                               @FormDataParam("file") FormDataContentDisposition fileDisposition,
                               @FormDataParam("image") InputStream imageStream,
                               @FormDataParam("image") FormDataContentDisposition imageDisposition,
                               @FormDataParam("bookInfo") FormDataBodyPart bookPart,
                               @Context final HttpServletRequest request){
        if(bookPart==null){
            log.warn("bookInfo is empty");
            return Response.status(Response.Status.BAD_REQUEST).entity("bookInfo is empty").build();
        }
        //converting bookInfo json to book pojo
        bookPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
        Book book=bookPart.getValueAs(Book.class);

        try{
            //validating the book
            ObjectValidator.validateFields(book);
        }catch(ValidationException e){
            log.warn("Invalid bookInfo");
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422).entity("Invalid bookInfo").build();
        }

        if(bookService.findById(book.getId())==null){
            log.warn("Book cannot be found. BookId: "+book.getId());
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }

        bookService.update(book);

        //adding file to book
        try {
            if(fileStream!=null)
                bookService.addFileToBook(book,new StoredFile(fileStream,fileDisposition.getFileName()),request.getContentLengthLong());
        }  catch (IOException e) {
            log.warn("Cannot read the file");
        } catch(IllegalArgumentException e){
            log.warn(e.getMessage());
        } catch(FileTooLargeException e){
            log.warn(e.getMessage());
        }

        //adding image to book
        try {
            if(imageStream!=null)
                bookService.addImageToBook(book,new StoredFile(imageStream,imageDisposition.getFileName()),request.getContentLengthLong());
        }  catch (IOException e) {
            log.warn("Cannot read the image");
        } catch(IllegalArgumentException e){
            log.warn(e.getMessage());
        } catch(FileTooLargeException e){
            log.warn(e.getMessage());
        }

        return Response.status(Response.Status.OK).entity(book).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Long id){
        if(bookService.findById(id)!=null){
            bookService.delete(id);
            return Response.ok().build();
        }
        else{
            log.warn("Book cannot be found. BookId: "+id);
            return Response.status(Response.Status.NOT_FOUND).entity("Book cannot be found").build();
        }
    }
}
