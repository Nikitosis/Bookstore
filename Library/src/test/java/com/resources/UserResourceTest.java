package com.resources;

import com.MainConfig;
import com.crossapi.models.Book;
import com.crossapi.services.OktaService;
import com.dao.BookDao;
import com.crossapi.dao.RoleDao;
import com.crossapi.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.crossapi.models.Role;
import com.crossapi.models.User;
import com.services.BookService;
import com.services.UserService;
import com.services.storage.AwsStorageService;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Validator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Integration test for BookResource
 */
@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {
    //Building MainConfig
    private final ObjectMapper objectMapper = Jackson.newObjectMapper();
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<MainConfig> factory = new YamlConfigurationFactory<>(MainConfig.class,validator,objectMapper,"dw");
    private final File yaml=new File(Thread.currentThread().getContextClassLoader().getResource("test-configuration.yml").getPath());
    private final MainConfig configuration=factory.build(yaml);

    private final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    //Creating mocks
    private UserDao userDao =mock(UserDao.class);
    private BookDao bookDao=mock(BookDao.class);
    private RoleDao roleDao=mock(RoleDao.class);
    private Authentication authentication=mock(Authentication.class);

    //Creating dependencies
    private AwsStorageService awsStorageService=mock(AwsStorageService.class);
    private OktaService oktaService=new OktaService(configuration.getOktaOAuth());
    private UserService userService =new UserService(userDao,roleDao,awsStorageService,passwordEncoder,configuration);
    private BookService bookService=spy(new BookService(bookDao,configuration,oktaService,awsStorageService));

    //Creating ResourceTestRule
    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new UserResource(userService,bookService,roleDao))
            .build();

    //Test entities
    private User testUser;
    private User anotherTestUser;
    private Book testBook;

    public UserResourceTest() throws IOException, ConfigurationException {
    }

    @Before
    public void init(){
        testUser =new User();
        testUser.setId(12L);
        testUser.setPassword("password");
        testUser.setUsername("username");
        testUser.setfName("FNAME");
        testUser.setlName("LNAME");

        anotherTestUser=new User();
        anotherTestUser.setId(13L);
        anotherTestUser.setPassword("password1");
        anotherTestUser.setUsername("username1");
        anotherTestUser.setfName("FNAME1");
        anotherTestUser.setlName("LNAME1");

        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");

        //building security mocks
        SecurityContext securityContext=mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getUsersTest(){
        List<User> users= Arrays.asList(testUser);
        when(userDao.findAll()).thenReturn(users);

        List<User> responseUsers =resources.target("/users")
                .request()
                .get(new GenericType<List<User>>(){});

        Assert.assertArrayEquals(users.toArray(), responseUsers.toArray());
    }

    @Test
    public void getUserByIdTest(){
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);

        User responseUser =resources.target("/users/"+ testUser.getId())
                .request()
                .get(User.class);

        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void getUserByIdTest_userNotFound(){
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addUserTest(){
        when(userDao.save(any(User.class))).thenReturn(1L);
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);

        User responseUser =resources.target("/users")
                .request()
                .post(Entity.entity(testUser, MediaType.APPLICATION_JSON), User.class);

        verify(userDao).save(eq(testUser));
        verify(roleDao).addUserRole(eq(testUser.getId()),eq("USER"));
        Assert.assertEquals(testUser, responseUser);
        //compare raw testUser's password to hashed responseUser's password
        Assert.assertTrue(passwordEncoder.matches(testUser.getPassword(),responseUser.getPassword()));
    }

    @Test
    public void updateUserTest(){
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);

        User responseUser = resources.target("/users")
                .request()
                .put(Entity.entity(testUser,MediaType.APPLICATION_JSON), User.class);

        verify(userDao).update(eq(testUser));
        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void updateUserTest_userNotFound(){
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users")
                .request()
                .put(Entity.entity(testUser,MediaType.APPLICATION_JSON))
                .getStatusInfo();

        verify(userDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void deleteUserTest(){
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(userDao).delete(eq(testUser.getId()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteUserTest_userNotFound(){
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(userDao,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getTakenUsersBooksTest(){
        List<Book> testBooks=Arrays.asList(testBook);
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findTakenByUser(eq(testUser.getId()))).thenReturn(testBooks);

        List<Book> responseBooks=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get(new GenericType<List<Book>>(){});

        Assert.assertArrayEquals(testBooks.toArray(),responseBooks.toArray());
    }

    @Test
    public void getTakenUsersBooksTest_wrongUser(){
        List<Book> testBooks=Arrays.asList(testBook);
        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findTakenByUser(eq(testUser.getId()))).thenReturn(testBooks);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.FORBIDDEN,responseStatus);
    }

    @Test
    public void getTakenUsersBooksTest_wrongUserButAdmin(){
        List<Book> testBooks=Arrays.asList(testBook);

        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);
        when(roleDao.findByUser(anotherTestUser.getId())).thenReturn(Arrays.asList(new Role(1L,"ADMIN")));

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findTakenByUser(eq(testUser.getId()))).thenReturn(testBooks);

        List<Book> responseBooks=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get(new GenericType<List<Book>>(){});

        Assert.assertArrayEquals(testBooks.toArray(),responseBooks.toArray());
    }

    @Test
    public void getTakenUserBooksTest_userNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        doNothing().when(bookService).postUserBookLog(any());

        resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""));

        verify(bookService).postUserBookLog(any());
        //verify(bookDao).takeBook(eq(testUser.getId()),eq(testBook.getId()));
    }

    @Test
    public void takeBookTest_wrongUser(){
        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testUser.getId()))).thenReturn(testBook);
        //when(bookDao.isTaken(eq(testUser.getId()))).thenReturn(false);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus = resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        Assert.assertEquals(Response.Status.FORBIDDEN,responseStatus);
        verify(bookService,times(0)).postUserBookLog(any());
        //verify(bookDao,times(0)).takeBook(eq(testUser.getId()),eq(testBook.getId()));
    }

    @Test
    public void takeBookTest_userNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(anyLong())).thenReturn(null);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        //verify(bookDao,times(0)).takeBook(anyLong(),anyLong());
        verify(bookService,times(0)).postUserBookLog(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest_bookNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(anyLong())).thenReturn(null);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        //verify(bookDao,times(0)).takeBook(anyLong(),anyLong());
        verify(bookService,times(0)).postUserBookLog(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBootTest_bookAlreadyTaken(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        //when(bookDao.isTaken(eq(testBook.getId()))).thenReturn(true);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        //verify(bookDao,times(0)).takeBook(anyLong(),anyLong());
        verify(bookService,times(0)).postUserBookLog(any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void returnBookTest(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(true);

        doNothing().when(bookService).postUserBookLog(any());

        resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete();

        verify(bookService).postUserBookLog(any());
        verify(bookDao).returnBook(eq(testUser.getId()),eq(testBook.getId()));
    }

    @Test
    public void returnBookTest_wrongUser(){
        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testUser.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(true);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(eq(testUser.getId()),eq(testBook.getId()));
        verify(bookService,times(0)).postUserBookLog(any());
        Assert.assertEquals(Response.Status.FORBIDDEN,responseStatus);
    }

    @Test
    public void returnBookTest_userNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(anyLong())).thenReturn(null);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        verify(bookService,times(0)).postUserBookLog(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBookTest_bookNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(anyLong())).thenReturn(null);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        verify(bookService,times(0)).postUserBookLog(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBootTest_bookNotTaken(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(false);

        doNothing().when(bookService).postUserBookLog(any());

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        verify(bookService,times(0)).postUserBookLog(any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }
}
