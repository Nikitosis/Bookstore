package com.resources;

import com.amazonaws.services.codecommit.model.FileTooLargeException;
import com.amazonaws.util.IOUtils;
import com.crossapi.dao.RoleDao;
import com.crossapi.models.Book;
import com.crossapi.models.User;
import com.services.BookService;
import com.services.UserService;
import com.services.storage.StoredFile;
import io.swagger.annotations.Api;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@Api(value = "/users")
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger log= LoggerFactory.getLogger(UserResource.class);

    private UserService userService;

    private BookService bookService;

    private RoleDao roleDao;

    @Autowired
    public UserResource(UserService userService, BookService bookService, RoleDao roleDao) {
        this.userService = userService;
        this.bookService = bookService;
        this.roleDao = roleDao;
    }

    @GET
    public Response getUsers(){
        return Response.ok(userService.findAll()).build();
    }

    @GET
    @Path("/{userId}")
    public Response getUserById(@PathParam("userId") Long userId){
        User user = userService.findById(userId);
        if(user==null){
            log.warn("User cannot be found: "+userId);
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }

        return Response.ok(user).build();
    }

    @PUT
    @Path("/{userId}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response setUserImage(@PathParam("userId")Long userId,
                                 @FormDataParam("file")InputStream fileStream,
                                 @FormDataParam("file")FormDataContentDisposition fileDisposition){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        if(principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        User user=userService.findById(userId);
        if(user==null) {
            log.warn("User cannot be found: " + userId);
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }

        if(!tryAddImageToUser(user,fileStream,fileDisposition)){
            log.warn("Failed to add image to user");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.OK).entity(user).build();
    }

    @POST
    public Response addUser(@NotNull @Valid User user){
        if(userService.findByUsername(user.getUsername())!=null){
            log.warn("User already exists with this username: "+user.getUsername());
            return Response.status(Response.Status.CONFLICT).build();
        }

        userService.save(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    public Response updateUser(@NotNull @Valid User user){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        if(principalUser.getId()!=user.getId()){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+user.getId());
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        if(userService.findById(user.getId())==null){
            log.warn("User cannot be found: "+user.getId());
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }

        userService.update(user);
        return Response.ok(userService.findById(user.getId())).build();
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") Long userId){
        if(userService.findById(userId)==null){
            log.warn("User cannot be found: "+userId);
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }

        userService.delete(userId);
        return Response.ok().build();
    }

    @GET
    @Path("/{userId}/books")
    public Response getTakenUsersBooks(@PathParam("userId") Long userId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        //if not admin and tries to view other's books
        if(!roleDao.findByUser(principalUser.getId()).stream().anyMatch(role -> role.getName().equals("ADMIN")) && principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        if(userService.findById(userId)==null){
            log.warn("User cannot be found: "+userId);
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }

        List<Book> takenBooks=bookService.findTakenByUser(userId);
        return Response.ok().entity(takenBooks).build();
    }

    @GET
    @Path("/{userId}/books/{bookId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getBook(@PathParam("userId") Long userId,
                             @PathParam("bookId") Long bookId,
                             @QueryParam(value = "returnDate") Optional<String> returnDateStr){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        if(principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        //if book or user cannot be found
        if(bookService.findById(bookId)==null || userService.findById(userId)==null){
            log.warn("Book or User cannot be found. BookId: "+bookId+" .UserId: "+userId);
            return Response.status(Response.Status.NOT_FOUND).entity("Book or user cannot be found").build();
        }

        //if book is not taken
        if(!bookService.isTakenByUser(userId,bookId)){
            log.warn("Book is not taken by this user. BookId: "+bookId+" .UserId: "+userId);
            return Response.status(Response.Status.BAD_REQUEST).entity("Book is not taken by this user").build();
        }


        try {
            StoredFile storedFile=bookService.getStoredFile(bookId);
            return Response.status(Response.Status.OK)
                    .entity(IOUtils.toByteArray(storedFile.getInputStream()))
                    .header("Content-Disposition", "attachment; filename=" + storedFile.getFileName())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("Error processing the file");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/{userId}/books/{bookId}")
    public Response takeBook(@PathParam("userId") Long userId,
                             @PathParam("bookId") Long bookId,
                             @QueryParam(value = "returnDate") Optional<String> returnDateStr){
        LocalDate returnDate=null;
        try{
            if(returnDateStr.isPresent())
                returnDate=LocalDate.parse(returnDateStr.get());
        }
        catch (DateTimeParseException e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong format of returnDate").build();
        }

        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        if(principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        //if book or user cannot be found
        if(bookService.findById(bookId)==null || userService.findById(userId)==null){
            log.warn("Book or User cannot be found. BookId: "+bookId+" .UserId: "+userId);
            return Response.status(Response.Status.NOT_FOUND).entity("Book or user cannot be found").build();
        }

        //if book is already taken
        if(bookService.isTakenByUser(userId,bookId)){
            log.warn("Book already taken by this user. BookId: "+bookId+" .UserId: "+userId);
            return Response.status(Response.Status.BAD_REQUEST).entity("Book is already taken").build();
        }

        bookService.takeBook(userId,bookId,returnDate);
        return Response.ok().build();

    }

    @DELETE
    @Path("/{userId}/books/{bookId}")
    public Response returnBook(@PathParam("userId") Long userId,
                               @PathParam("bookId") Long bookId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        if(principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        //if book or user cannot be found
        if(bookService.findById(bookId)==null || userService.findById(userId)==null){
            log.warn("Book or User cannot be found. BookId: "+bookId+" .UserId: "+userId);
            return Response.status(Response.Status.NOT_FOUND).entity("Book or user cannot be found").build();
        }

        //if book is not taken
        if(!bookService.isTakenByUser(userId,bookId)){
            log.warn("Book is not taken by this user. BookId: "+bookId+" .UserId: "+userId);
            return Response.status(Response.Status.BAD_REQUEST).entity("Book is not taken by this user").build();
        }


        bookService.returnBook(userId,bookId);
        return Response.ok().build();

    }

    private boolean tryAddImageToUser(User user,
                                      InputStream fileStream,
                                      FormDataContentDisposition fileDisposition){
        try {
            if(fileStream!=null) {
                userService.setUserImage(user, new StoredFile(fileStream, fileDisposition.getFileName()));
                return true;
            }
        }  catch (IOException e) {
            log.warn("Cannot read the file");
        } catch(IllegalArgumentException e){
            log.warn(e.getMessage());
        } catch(FileTooLargeException e){
            log.warn(e.getMessage());
        }
        return false;
    }
}
