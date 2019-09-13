package com.resources;

import com.MainConfig;
import com.dao.BookDao;
import com.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.Book;
import com.models.User;
import com.services.BookService;
import com.services.UserService;
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
    final ObjectMapper objectMapper = Jackson.newObjectMapper();
    final Validator validator = Validators.newValidator();
    final YamlConfigurationFactory<MainConfig> factory = new YamlConfigurationFactory<>(MainConfig.class,validator,objectMapper,"dw");
    final File yaml=new File(Thread.currentThread().getContextClassLoader().getResource("test-configuration.yml").getPath());
    final MainConfig configuration=factory.build(yaml);

    //Creating mocks
    private UserDao userDao =mock(UserDao.class);
    private BookDao bookDao=mock(BookDao.class);

    //Creating dependencies
    private UserService userService =new UserService(userDao);
    private BookService bookService=new BookService(bookDao,configuration);

    //Creating ResourceTestRule
    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new UserResource(userService,bookService))
            .build();

    //Test entities
    private User testUser;
    private Book testBook;

    public UserResourceTest() throws IOException, ConfigurationException {
    }

    @Before
    public void init(){
        testUser =new User();
        testUser.setPassword("password");
        testUser.setUsername("username");
        testUser.setfName("FNAME");
        testUser.setlName("LNAME");

        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
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
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        User responseUser =resources.target("/users/"+ testUser.getUsername())
                .request()
                .get(User.class);

        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void getUserByIdTest_userNotFound(){
        when(userDao.findByUsername(anyString())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getUsername())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addUserTest(){
        when(userDao.save(any(User.class))).thenReturn(1L);
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        User responseUser =resources.target("/users")
                .request()
                .post(Entity.entity(testUser, MediaType.APPLICATION_JSON), User.class);

        verify(userDao).save(eq(testUser));
        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void updateUserTest(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        User responseUser = resources.target("/users")
                .request()
                .put(Entity.entity(testUser,MediaType.APPLICATION_JSON), User.class);

        verify(userDao).update(eq(testUser));
        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void updateUserTest_userNotFound(){
        when(userDao.findByUsername(anyString())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users")
                .request()
                .put(Entity.entity(testUser,MediaType.APPLICATION_JSON))
                .getStatusInfo();

        verify(userDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void deleteUserTest(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername())
                .request()
                .delete()
                .getStatusInfo();

        verify(userDao).delete(eq(testUser.getUsername()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteUserTest_userNotFound(){
        when(userDao.findByUsername(anyString())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getUsername())
                .request()
                .delete()
                .getStatusInfo();

        verify(userDao,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getTakenUsersBooksTest(){
        List<Book> testBooks=Arrays.asList(testBook);
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findTakenByUsername(eq(testUser.getUsername()))).thenReturn(testBooks);

        List<Book> responseBooks=resources.target("/users/"+ testUser.getUsername()+"/books")
                .request()
                .get(new GenericType<List<Book>>(){});

        Assert.assertArrayEquals(testBooks.toArray(),responseBooks.toArray());
    }

    @Test
    public void getTakenUserBooksTest_cuserNotFound(){
        when(userDao.findByUsername(anyString())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername()+"/books")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTaken(eq(testBook.getId()))).thenReturn(false);

        resources.target("/users/"+ testUser.getUsername()+"/books")
               .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""));

        verify(bookDao).takeBook(eq(testUser.getUsername()),eq(testBook.getId()));
    }

    @Test
    public void takeBookTest_userNotFound(){
        when(userDao.findByUsername(anyString())).thenReturn(null);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookDao,times(0)).takeBook(anyString(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest_bookNotFound(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookDao,times(0)).takeBook(anyString(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBootTest_bookAlreadyTaken(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTaken(eq(testBook.getId()))).thenReturn(true);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookDao,times(0)).takeBook(anyString(),anyLong());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void returnBookTest(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getUsername()),eq(testBook.getId()))).thenReturn(true);

        resources.target("/users/"+ testUser.getUsername()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete();

        verify(bookDao).returnBook(eq(testUser.getUsername()),eq(testBook.getId()));
    }

    @Test
    public void returnBookTest_userNotFound(){
        when(userDao.findByUsername(anyString())).thenReturn(null);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyString(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBookTest_bookNotFound(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyString(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBootTest_bookNotTaken(){
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getUsername()),eq(testBook.getId()))).thenReturn(false);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getUsername()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyString(),anyLong());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }
}
