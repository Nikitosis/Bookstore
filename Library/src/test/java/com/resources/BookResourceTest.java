package com.resources;

import com.MainConfig;
import com.dao.BookDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.Book;
import com.services.BookService;
import com.services.OktaService;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.*;
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
public class BookResourceTest {
    //Building MainConfig
    final ObjectMapper objectMapper = Jackson.newObjectMapper();
    final Validator validator = Validators.newValidator();
    final YamlConfigurationFactory<MainConfig> factory = new YamlConfigurationFactory<>(MainConfig.class,validator,objectMapper,"dw");
    final File yaml=new File(Thread.currentThread().getContextClassLoader().getResource("test-configuration.yml").getPath());
    final MainConfig configuration=factory.build(yaml);

    //Creating mocks
    private BookDao bookDao=mock(BookDao.class);

    //Creating dependencies
    private OktaService oktaService=new OktaService(configuration);
    private BookService bookService=new BookService(bookDao,configuration,oktaService);

    //Creating ResourceTestRule
    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new BookResource(bookService))
            .build();

    //Test entities
    private Book testBook;

    public BookResourceTest() throws IOException, ConfigurationException {
    }

    @Before
    public void init() {
        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
    }


    @Test
    public void getBooksTest(){
        List<Book> books=Arrays.asList(testBook);
        when(bookDao.findAll()).thenReturn(books);

       List<Book> responseBooks=resources.target("/books")
               .request()
               .get(new GenericType<List<Book>>(){});

       Assert.assertArrayEquals(books.toArray(),responseBooks.toArray());
    }

    @Test
    public void getBookByIdTest(){
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        Book responseBook=resources.target("/books/"+testBook.getId())
                .request()
                .get(Book.class);

        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void getBookByIdTest_bookNotFound(){
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/books/2")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addBookTest(){
        when(bookDao.save(any(Book.class))).thenReturn(1L);

        Book responseBook=resources.target("/books")
                .request()
                .post(Entity.entity(testBook,MediaType.APPLICATION_JSON),Book.class);

        verify(bookDao).save(eq(testBook));
        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void updateBookTest(){
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        Book responseBook=resources.target("/books")
                .request()
                .put(Entity.entity(testBook,MediaType.APPLICATION_JSON),Book.class);

        verify(bookDao).update(eq(testBook));
        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void updateBookTest_bookNotFound(){
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books")
                .request()
                .put(Entity.entity(testBook,MediaType.APPLICATION_JSON)).getStatusInfo();

        verify(bookDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void deleteBookTest(){
        when(bookDao.findById(anyLong())).thenReturn(testBook);

        Response.StatusType responseStatus= resources.target("/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao).delete(eq(testBook.getId()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteBookTest_bookNotFound(){
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

}
