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
    @Path("/{username}")
    public Response getUserByUsername(@PathParam("username") String username){
        User user = userService.findByUsername(username);
        if(user !=null){
            return Response.ok(user).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @POST
    public Response addUser(@Valid User user){
        userService.save(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    public Response updateUser(@Valid User user){
        if(userService.findByUsername(user.getUsername())!=null){
            userService.update(user);
            return Response.ok(userService.findByUsername(user.getUsername())).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @DELETE
    @Path("/{username}")
    public Response deleteUser(@PathParam("username") String username){
        if(userService.findByUsername(username)!=null){
            userService.delete(username);
            return Response.ok().build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @GET
    @Path("/{username}/books")
    public Response getTakenUsersBooks(@PathParam("username") String username){
        if(userService.findByUsername(username)!=null){
            List<Book> takenBooks=bookService.findTakenByUsername(username);
            return Response.ok().entity(takenBooks).build();
        }
        else{
            return Response.status(Response.Status.NOT_FOUND).entity("User cannot be found").build();
        }
    }

    @PUT
    @Path("/{username}/books")
    public Response takeBook(@PathParam("username") String username,
                             @QueryParam("bookId") Long bookId){
        if(bookService.findById(bookId)!=null && userService.findByUsername(username)!=null){
            if(!bookService.isTaken(bookId)){
                bookService.takeBook(username,bookId);
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
    @Path("/{username}/books")
    public Response returnBook(@PathParam("username") String username,
                               @QueryParam("bookId") Long bookId){
        if(bookService.findById(bookId)!=null && userService.findByUsername(username)!=null){
            if(bookService.isTakenByUser(username,bookId)){
                bookService.returnBook(username,bookId);
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
