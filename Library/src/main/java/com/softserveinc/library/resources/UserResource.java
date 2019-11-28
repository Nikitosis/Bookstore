package com.softserveinc.library.resources;

import com.amazonaws.services.codecommit.model.FileTooLargeException;
import com.amazonaws.util.Base64;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import com.softserveinc.cross_api_objects.dao.CountryDao;
import com.softserveinc.cross_api_objects.dao.RoleDao;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.ResponseError;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.library.models.Deposit;
import com.softserveinc.library.services.BookService;
import com.softserveinc.library.services.UserService;
import com.softserveinc.cross_api_objects.services.storage.StoredFile;
import com.softserveinc.library.utils.ObjectValidator;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
//@Api(value = "/users")
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final Logger log= LoggerFactory.getLogger(UserResource.class);

    private UserService userService;

    private BookService bookService;

    private RoleDao roleDao;

    private CountryDao countryDao;

    @Autowired
    public UserResource(UserService userService, BookService bookService, RoleDao roleDao, CountryDao countryDao) {
        this.userService = userService;
        this.bookService = bookService;
        this.roleDao = roleDao;
        this.countryDao = countryDao;
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
            ResponseError error=new ResponseError().setCode(1).setMessage("User cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(user).build();
    }

    @GET
    @Path("/me")
    public Response getMe(){
        User principalUser=getAuthenticatedUser();
        
        return Response.status(Response.Status.OK).entity(principalUser).build();
    }

    @PUT
    @Path("/{userId}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response setUserImage(@PathParam("userId")Long userId,
                                 @FormDataParam("image")InputStream fileStream,
                                 @FormDataParam("image")FormDataContentDisposition fileDisposition){
        User principalUser= getAuthenticatedUser();

        if(principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            ResponseError error=new ResponseError().setCode(1).setMessage("User is not authorised to access this resource");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        User user=userService.findById(userId);
        if(user==null) {
            log.warn("User cannot be found: " + userId);
            ResponseError error=new ResponseError().setCode(2).setMessage("User cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        if(!tryAddImageToUser(user,fileStream,fileDisposition)){
            log.warn("Failed to add image to user");
            ResponseError error=new ResponseError().setCode(3).setMessage("Failed to add image to user");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        userService.update(user);

        return Response.status(Response.Status.OK).entity(user).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addUser(@FormDataParam("image") InputStream imageStream,
                            @FormDataParam("image") FormDataContentDisposition imageDisposition,
                            @FormDataParam("userInfo") FormDataBodyPart userPart){
        User user= convertBodyPartToUser(userPart);

        if(user==null){
            log.warn("UserInfo is not valid");
            ResponseError error=new ResponseError().setCode(1).setMessage("UserInfo is not valid");
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422).entity(error).build();
        }

        if(userService.findByUsername(user.getUsername())!=null){
            log.warn("User already exists with this username: "+user.getUsername());
            ResponseError error=new ResponseError().setCode(2).setMessage("User already exists with this username");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if(userService.findByEmail(user.getEmail())!=null){
            log.warn("This email is already in use");
            ResponseError error=new ResponseError().setCode(3).setMessage("Email already in use");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if(!StringUtils.isNullOrEmpty(user.getCountry()) && countryDao.findByName(user.getCountry())==null){
            log.warn("Country is not valid");
            ResponseError error=new ResponseError().setCode(4).setMessage("Country is not valid");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if(!isEmailValid(user.getEmail())){
            log.warn("Email is not valid");
            ResponseError error=new ResponseError().setCode(5).setMessage("Email is not valid");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        tryAddImageToUser(user,imageStream,imageDisposition);

        userService.save(user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateUser(@FormDataParam("image") InputStream imageStream,
                               @FormDataParam("image") FormDataContentDisposition imageDisposition,
                               @FormDataParam("userInfo") FormDataBodyPart userPart){
        User user= convertBodyPartToUser(userPart);
        if(user==null){
            log.warn("UserInfo is not valid");
            ResponseError error=new ResponseError().setCode(1).setMessage("UserInfo is not valid");
            return Response.status(HttpStatus.UNPROCESSABLE_ENTITY_422).entity(error).build();
        }

        User principalUser= getAuthenticatedUser();

        if(principalUser.getId()!=user.getId()){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+user.getId());
            ResponseError error=new ResponseError().setCode(2).setMessage("User is not authorised to access this resource");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if(userService.findById(user.getId())==null){
            log.warn("User cannot be found: "+user.getId());
            ResponseError error=new ResponseError().setCode(3).setMessage("User cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        User userByEmail=userService.findByEmail(user.getEmail());
        if(userByEmail!=null && userByEmail.getId()!=user.getId()){
            log.warn("This email is already in use");
            ResponseError error=new ResponseError().setCode(4).setMessage("Email already in use");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if(!StringUtils.isNullOrEmpty(user.getCountry()) && countryDao.findByName(user.getCountry())==null){
            log.warn("Country is not valid");
            ResponseError error=new ResponseError().setCode(5).setMessage("Country is not valid");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if(!isEmailValid(user.getEmail())){
            log.warn("Email is not valid");
            ResponseError error=new ResponseError().setCode(6).setMessage("Email is not valid");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        tryAddImageToUser(user,imageStream,imageDisposition);

        userService.update(user);

        return Response.ok(userService.findById(user.getId())).build();
    }

    @PUT
    @Path("/{userId}/money")
    public Response depositMoney(@PathParam("userId") Long userId,
                                 Deposit deposit) throws IOException {
        User principalUser=getAuthenticatedUser();
        if(principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            ResponseError error=new ResponseError().setCode(2).setMessage("User is not authorised to access this resource");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        userService.depositMoney(principalUser,deposit);

        return Response.ok(principalUser).build();
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") Long userId){
        if(userService.findById(userId)==null){
            log.warn("User cannot be found: "+userId);
            ResponseError error=new ResponseError().setCode(1).setMessage("User cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        userService.delete(userId);
        return Response.ok().build();
    }

    @GET
    @Path("/{userId}/books")
    public Response getTakenUsersBooks(@PathParam("userId") Long userId){
        User principalUser= getAuthenticatedUser();

        //if not admin and tries to view other's books
        if(!roleDao.findByUser(principalUser.getId()).stream().anyMatch(role -> role.getName().equals("ADMIN")) && principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            ResponseError error=new ResponseError().setCode(1).setMessage("User is not authorised to access this resource");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if(userService.findById(userId)==null){
            log.warn("User cannot be found: "+userId);
            ResponseError error=new ResponseError().setCode(2).setMessage("User cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        List<Book> takenBooks=bookService.findTakenByUser(userId);
        return Response.ok().entity(takenBooks).build();
    }

    @GET
    @Path("/{userId}/books/{bookId}")
    public Response getBook(@PathParam("userId") Long userId,
                             @PathParam("bookId") Long bookId){
        User principalUser= getAuthenticatedUser();

        if(principalUser.getId()!=userId){
            log.warn("User is not authorised to access this resource. Principal id: "+principalUser.getId()+". Resource's User id: "+userId);
            ResponseError error=new ResponseError().setCode(1).setMessage("User is not authorised to access this resource");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        //if book or user cannot be found
        if(bookService.findById(bookId)==null || userService.findById(userId)==null){
            log.warn("Book or User cannot be found. BookId: "+bookId+" .UserId: "+userId);
            ResponseError error=new ResponseError().setCode(2).setMessage("Book or User cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        //if book is not taken
        if(!bookService.isTakenByUser(userId,bookId)){
            log.warn("Book is not taken by this user. BookId: "+bookId+" .UserId: "+userId);
            ResponseError error=new ResponseError().setCode(3).setMessage("Book is not taken by this user");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }


        try {
            StoredFile storedFile=bookService.getStoredFile(bookId);
            byte[] bytes = IOUtils.toByteArray(storedFile.getInputStream());
            byte[] encoded = Base64.encode(bytes);
            String encodedStr = new String(encoded);
            return Response.status(Response.Status.OK)
                    .entity(encodedStr)
                    .header("Content-Disposition", "attachment; filename=" + storedFile.getFileName())
                    .build();
        } catch (Exception e) {
            log.warn("Error processing the file");
            ResponseError error=new ResponseError().setCode(4).setMessage("Error processing the file");
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
            ResponseError error=new ResponseError().setCode(1).setMessage("Wrong format of returnDate");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        Authentication auth=SecurityContextHolder.getContext().getAuthentication();

        if(!isAuthorisedManageBooks(auth,userId)){
            log.warn("User is not authorised to access this resource. Resource's User id: "+userId);
            ResponseError error=new ResponseError().setCode(2).setMessage("User is not authorised to access this resource.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        //if book or user cannot be found
        if(bookService.findById(bookId)==null || userService.findById(userId)==null){
            log.warn("Book or User cannot be found. BookId: "+bookId+" .UserId: "+userId);
            ResponseError error=new ResponseError().setCode(3).setMessage("Book or user cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity("Book or user cannot be found").build();
        }

        //if book is already taken
        if(bookService.isTakenByUser(userId,bookId)){
            log.warn("Book already taken by this user. BookId: "+bookId+" .UserId: "+userId);
            ResponseError error=new ResponseError().setCode(4).setMessage("Book is already taken");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if(bookService.findById(bookId).getPrice().compareTo(userService.findById(userId).getMoney())==1){
            log.warn("Cannot take book. User doesn't have enough money");
            ResponseError error=new ResponseError().setCode(5).setMessage("User doesn't have enough money");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        bookService.takeBook(userId,bookId,null);
        return Response.ok().build();

    }

    @DELETE
    @Path("/{userId}/books/{bookId}")
    public Response returnBook(@PathParam("userId") Long userId,
                               @PathParam("bookId") Long bookId){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();

        if(!isAuthorisedManageBooks(auth,userId)){
            log.warn("User is not authorised to access this resource. Resource's User id: "+userId);
            ResponseError error=new ResponseError().setCode(1).setMessage("User is not authorised to access this resource");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        //if book or user cannot be found
        if(bookService.findById(bookId)==null || userService.findById(userId)==null){
            log.warn("Book or User cannot be found. BookId: "+bookId+" .UserId: "+userId);
            ResponseError error=new ResponseError().setCode(2).setMessage("Book or User cannot be found");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        //if book is not taken
        if(!bookService.isTakenByUser(userId,bookId)){
            log.warn("Book is not taken by this user. BookId: "+bookId+" .UserId: "+userId);
            ResponseError error=new ResponseError().setCode(3).setMessage("Book is not taken by this user");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }


        bookService.returnBook(userId,bookId);
        return Response.ok().build();
    }

    private User getAuthenticatedUser(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    private boolean isAuthorisedManageBooks(Authentication auth, Long userId){
        //if request is from service
        if(auth.getAuthorities().stream().anyMatch(authority -> ((GrantedAuthority) authority).getAuthority().equals("write")))
            return true;

        User principalUser= userService.findByUsername(auth.getName());
        if(principalUser.getId()==userId)
            return true;

        return false;
    }

    private User convertBodyPartToUser(FormDataBodyPart userPart){
        try {
            //converting bookInfo json to book pojo
            userPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            User user = userPart.getValueAs(User.class);

            //validating the book
            ObjectValidator.validateFields(user);

            return user;

        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    private boolean tryAddImageToUser(User user,
                                      InputStream fileStream,
                                      FormDataContentDisposition fileDisposition){
        try {
            if(fileStream!=null) {
                String url=userService.setUserImage(new StoredFile(fileStream, fileDisposition.getFileName()));
                user.setAvatarLink(url);
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

    private boolean isEmailValid(String email){
        if(StringUtils.isNullOrEmpty(email)){
            return true;
        }
        return Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
                .matcher(email)
                .find();
    }
}
