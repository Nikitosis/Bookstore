package com.resources;

import com.models.Book;
import com.models.Client;
import com.services.BookService;
import com.services.ClientService;
import io.dropwizard.cli.Cli;
import io.dropwizard.testing.junit.ResourceTestRule;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientResourceTest {
    private ClientService clientService=mock(ClientService.class);
    private BookService bookService=mock(BookService.class);

    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new ClientResource(clientService,bookService))
            .build();

    private Client testClient;
    private Book testBook;

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
        when(clientService.findAll()).thenReturn(clients);

        List<Client> responseClients=resources.target("/clients")
                .request()
                .get(new GenericType<List<Client>>(){});

        Assert.assertArrayEquals(clients.toArray(),responseClients.toArray());
    }

    @Test
    public void getClientByIdTest(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);

        Client responseClient=resources.target("/clients/"+testClient.getId())
                .request()
                .get(Client.class);

        Assert.assertEquals(testClient,responseClient);
    }

    @Test
    public void getClientByIdTest_clientNotFound(){
        when(clientService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/2")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addClientTest(){
        when(clientService.save(any(Client.class))).thenReturn(1L);
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);

        Client responseClient=resources.target("/clients")
                .request()
                .post(Entity.entity(testClient, MediaType.APPLICATION_JSON),Client.class);

        verify(clientService).save(eq(testClient));
        Assert.assertEquals(testClient,responseClient);
    }

    @Test
    public void updateClientTest(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);

        Client responseClient = resources.target("/clients")
                .request()
                .put(Entity.entity(testClient,MediaType.APPLICATION_JSON),Client.class);

        verify(clientService).update(eq(testClient));
        Assert.assertEquals(testClient,responseClient);
    }

    @Test
    public void updateClientTest_clientNotFound(){
        when(clientService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients")
                .request()
                .put(Entity.entity(testClient,MediaType.APPLICATION_JSON))
                .getStatusInfo();

        verify(clientService,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void deleteClientTest(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(clientService).delete(eq(testClient.getId()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteClientTest_clientNotFound(){
        when(clientService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/2")
                .request()
                .delete()
                .getStatusInfo();

        verify(clientService,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getTakenClientBooksTest(){
        List<Book> testBooks=Arrays.asList(testBook);
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookService.findTakenByClientId(eq(testClient.getId()))).thenReturn(testBooks);

        List<Book> responseBooks=resources.target("/clients/"+testClient.getId()+"/books")
                .request()
                .get(new GenericType<List<Book>>(){});

        Assert.assertArrayEquals(testBooks.toArray(),responseBooks.toArray());
    }

    @Test
    public void getTakenClientBooksTest_clientNotFound(){
        when(clientService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookService.isTaken(eq(testBook.getId()))).thenReturn(false);

        resources.target("/clients/"+testClient.getId()+"/books")
               .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""));

        verify(bookService).takeBook(eq(testClient.getId()),eq(testBook.getId()));
    }

    @Test
    public void takeBookTest_clientNotFound(){
        when(clientService.findById(anyLong())).thenReturn(null);
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookService,times(0)).takeBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest_bookNotFound(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookService,times(0)).takeBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBootTest_bookAlreadyTaken(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookService.isTaken(eq(testBook.getId()))).thenReturn(true);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookService,times(0)).takeBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void returnBookTest(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookService.isTakenByClient(eq(testClient.getId()),eq(testBook.getId()))).thenReturn(true);

        resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete();

        verify(bookService).returnBook(eq(testClient.getId()),eq(testBook.getId()));
    }

    @Test
    public void returnBookTest_clientNotFound(){
        when(clientService.findById(anyLong())).thenReturn(null);
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookService,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBookTest_bookNotFound(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookService,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBootTest_bookNotTaken(){
        when(clientService.findById(eq(testClient.getId()))).thenReturn(testClient);
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(bookService.isTakenByClient(eq(testClient.getId()),eq(testBook.getId()))).thenReturn(false);

        Response.StatusType responseStatus=resources.target("/clients/"+testClient.getId()+"/books")
                .queryParam("bookId",testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookService,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }
}
