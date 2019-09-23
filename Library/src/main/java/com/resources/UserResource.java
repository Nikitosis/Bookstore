package com.resources;

import com.models.Book;
import com.models.User;
import com.services.BookService;
import com.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Service
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserService userService;

    private BookService bookService;

    @Autowired
    public UserResource(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    @GET
    public Response getUsers(){
        return Response.ok(userService.findAll()).build();
    }

    @GET
    @Path("/{userId}")
    public Response getUserByUsername(@PathParam("userId") Long userId){
        User user = userService.findById(userId);
        if(user !=null){
            return Response.ok(user).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @POST
    public Response addUser(User user){
        userService.save(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    public Response updateUser(User user){
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
        if(userService.findById(userId)!=null){
            List<Book> takenBooks=bookService.findTakenByUser(userId);
            return Response.ok().entity(takenBooks).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @PUT
    @Path("/{userId}/books")
    public Response takeBook(@PathParam("userId") Long userId,
                             @QueryParam("bookId") Long bookId){
        if(bookService.findById(bookId)!=null && userService.findById(userId)!=null){
            if(!bookService.isTaken(bookId)){
                bookService.takeBook(userId,bookId);
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
    @Path("/{userId}/books")
    public Response returnBook(@PathParam("userId") Long userId,
                               @QueryParam("bookId") Long bookId){
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
