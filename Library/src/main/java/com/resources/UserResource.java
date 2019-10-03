package com.resources;

import com.amazonaws.util.IOUtils;
import com.crossapi.dao.RoleDao;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.models.Book;
import com.crossapi.models.User;
import com.services.BookService;
import com.services.UserService;
import com.services.storage.StoredFile;
import io.swagger.annotations.Api;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
@Api(value = "/users")
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
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
        if(user !=null){
            return Response.ok(user).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @PUT
    @Path("/{userId}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response setUserImage(@PathParam("userId")Long userId,
                                 @FormDataParam("file")InputStream fileStream,
                                 @FormDataParam("file")FormDataContentDisposition contentDisposition){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        if(principalUser.getId()!=userId){
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        User user=userService.findById(userId);
        if(user==null)
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();

        try {
            userService.setUserImage(user,new StoredFile(fileStream,contentDisposition.getFileName()));
            return Response.status(Response.Status.OK).entity(user).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Wrong file").build();
        }

    }

    @POST
    public Response addUser(@NotNull @Valid User user){
        if(userService.findByUsername(user.getUsername())==null){
            userService.save(user);
            return Response.status(Response.Status.CREATED).entity(user).build();
        }
        else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    //TODO: make only this user can update himself, not everyone
    @PUT
    public Response updateUser(@NotNull @Valid User user){
        if(userService.findById(user.getId())!=null){
            userService.update(user);
            return Response.ok(userService.findById(user.getId())).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") Long userId){
        if(userService.findById(userId)!=null){
            userService.delete(userId);
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @GET
    @Path("/{userId}/books")
    public Response getTakenUsersBooks(@PathParam("userId") Long userId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        //if not admin and tries to view other's books
        if(!roleDao.findByUser(principalUser.getId()).stream().anyMatch(role -> role.getName().equals("ADMIN")) && principalUser.getId()!=userId){
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        if(userService.findById(userId)!=null){
            List<Book> takenBooks=bookService.findTakenByUser(userId);
            return Response.ok().entity(takenBooks).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
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
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        if(bookService.findById(bookId)!=null && userService.findById(userId)!=null){
            if(bookService.isTakenByUser(userId,bookId)){
                try {
                    StoredFile storedFile=bookService.getStoredFile(bookId);
                    return Response.status(Response.Status.OK)
                            .entity(IOUtils.toByteArray(storedFile.getInputStream()))
                            .header("Content-Disposition", "attachment; filename=" + storedFile.getFileName())
                            .build();
                } catch (IOException e) {
                    e.printStackTrace();
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }
            else{
                //if book is already taken
                return Response.status(Response.Status.BAD_REQUEST).entity("Book is already taken").build();
            }
        }
        else{
            //if book or user cannot be found
            return Response.status(Response.Status.NOT_FOUND).entity("Book or user cannot be found").build();
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
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        if(bookService.findById(bookId)!=null && userService.findById(userId)!=null){
            if(!bookService.isTakenByUser(userId,bookId)){
                bookService.takeBook(userId,bookId,returnDate);
                return Response.ok().build();
            }
            else{
                //if book is already taken
                return Response.status(Response.Status.BAD_REQUEST).entity("Book is already taken").build();
            }
        }
        else{
            //if book or user cannot be found
            return Response.status(Response.Status.NOT_FOUND).entity("Book or user cannot be found").build();
        }
    }

    @DELETE
    @Path("/{userId}/books/{bookId}")
    public Response returnBook(@PathParam("userId") Long userId,
                               @PathParam("bookId") Long bookId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        User principalUser= userService.findByUsername(auth.getName());

        if(principalUser.getId()!=userId){
            return Response.status(Response.Status.FORBIDDEN).entity("User is not authorised to access this resource").build();
        }

        if(bookService.findById(bookId)!=null && userService.findById(userId)!=null){
            if(bookService.isTakenByUser(userId,bookId)){
                bookService.returnBook(userId,bookId);
                return Response.ok().build();
            }
            else{
                //if book is not taken by this user
                return Response.status(Response.Status.BAD_REQUEST).entity("Book is not taken by this user").build();
            }
        }
        else{
            //if book or user cannot be found
            return Response.status(Response.Status.NOT_FOUND).entity("Book or user cannot be found").build();
        }
    }
}
