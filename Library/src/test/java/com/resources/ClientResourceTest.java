package com.resources;

import com.MainConfig;
import com.dao.BookDao;
import com.dao.ClientDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.Book;
import com.models.Client;
import com.services.BookService;
import com.services.ClientService;
import io.dropwizard.cli.Cli;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.testing.junit.ResourceTestRule;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class ClientResourceTest {
    //Building MainConfig
    final ObjectMapper objectMapper = Jackson.newObjectMapper();
    final Validator validator = Validators.newValidator();
    final YamlConfigurationFactory<MainConfig> factory = new YamlConfigurationFactory<>(MainConfig.class,validator,objectMapper,"dw");
    final File yaml=new File(Thread.currentThread().getContextClassLoader().getResource("test-configuration.yml").getPath());
    final MainConfig configuration=factory.build(yaml);

    //Creating mocks
    private ClientDao clientDao=mock(ClientDao.class);
    private BookDao bookDao=mock(BookDao.class);

    //Creating dependencies
    private ClientService clientService=new ClientService(clientDao);
    private BookService bookService=new BookService(bookDao,configuration);

    //Creating ResourceTestRule
    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new ClientResource(clientService,bookService))
            .build();

    //Test entities
    private Client testClient;
    private Book testBook;

    public ClientResourceTest() throws IOException, ConfigurationException {
    }

    @Before
    public void init(){
        testClient=new Client();
        testClient.setId(12L);
        testClient.setfName("FNAME");
        testClient.setlName("LNAME");

        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
    }

    @Test
    public void getClientsTest(){
        List<Client> clients= Arrays.asList(testClient);
        when(clientDao.findAll()).thenReturn(clients);

        List<Client> responseClients=resources.target("/clients")
                .request()
                .get(new GenericType<List<Client>>(){});

        Assert.assertArrayEquals(clients.toArray(),responseClients.toArray());
    }

    @Test
    public void getClientByIdTest(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);

        Client responseClient=resources.target("/clients/"+testClient.getId())
                .request()
                .get(Client.class);

        Assert.assertEquals(testClient,responseClient);
    }

    @Test
    public void getClientByIdTest_clientNotFound(){
        when(clientDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/2")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addClientTest(){
        when(clientDao.save(any(Client.class))).thenReturn(1L);
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);

        Client responseClient=resources.target("/clients")
                .request()
                .post(Entity.entity(testClient, MediaType.APPLICATION_JSON),Client.class);

        verify(clientDao).save(eq(testClient));
        Assert.assertEquals(testClient,responseClient);
    }

    @Test
    public void updateClientTest(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);

        Client responseClient = resources.target("/clients")
                .request()
                .put(Entity.entity(testClient,MediaType.APPLICATION_JSON),Client.class);

        verify(clientDao).update(eq(testClient));
        Assert.assertEquals(testClient,responseClient);
    }

    @Test
    public void updateClientTest_clientNotFound(){
        when(clientDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients")
                .request()
                .put(Entity.entity(testClient,MediaType.APPLICATION_JSON))
                .getStatusInfo();

        verify(clientDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void deleteClientTest(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(clientDao).delete(eq(testClient.getId()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteClientTest_clientNotFound(){
        when(clientDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/2")
                .request()
                .delete()
                .getStatusInfo();

        verify(clientDao,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getTakenClientBooksTest(){
        List<Book> testBooks=Arrays.asList(testBook);
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookDao.findTakenByClientId(eq(testClient.getId()))).thenReturn(testBooks);

        List<Book> responseBooks=resources.target("/clients/"+testClient.getId()+"/books")
                .request()
                .get(new GenericType<List<Book>>(){});

        Assert.assertArrayEquals(testBooks.toArray(),responseBooks.toArray());
    }

    @Test
    public void getTakenClientBooksTest_clientNotFound(){
        when(clientDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTaken(eq(testBook.getId()))).thenReturn(false);

        resources.target("/clients/"+testClient.getId()+"/books")
               .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""));

        verify(bookDao).takeBook(eq(testClient.getId()),eq(testBook.getId()));
    }

    @Test
    public void takeBookTest_clientNotFound(){
        when(clientDao.findById(anyLong())).thenReturn(null);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookDao,times(0)).takeBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest_bookNotFound(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookDao,times(0)).takeBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBootTest_bookAlreadyTaken(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTaken(eq(testBook.getId()))).thenReturn(true);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookDao,times(0)).takeBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void returnBookTest(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByClient(eq(testClient.getId()),eq(testBook.getId()))).thenReturn(true);

        resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete();

        verify(bookDao).returnBook(eq(testClient.getId()),eq(testBook.getId()));
    }

    @Test
    public void returnBookTest_clientNotFound(){
        when(clientDao.findById(anyLong())).thenReturn(null);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBookTest_bookNotFound(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBootTest_bookNotTaken(){
        when(clientDao.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByClient(eq(testClient.getId()),eq(testBook.getId()))).thenReturn(false);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }
}
