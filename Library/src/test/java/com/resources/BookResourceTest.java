package com.resources;

import com.google.common.collect.Lists;
import com.models.Book;
import com.services.BookService;
import io.dropwizard.testing.junit.ResourceTestRule;
import net.sourceforge.argparse4j.inf.Argument;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookResourceTest {
    private BookService bookService=mock(BookService.class);

    @Rule
    public ResourceTestRule  resources = ResourceTestRule.builder()
            .addResource(new BookResource(bookService))
            .build();


    @Test
    public void getBooksTest(){
        Book book=new Book();
        book.setId(12L);
        book.setName("Name");
        List<Book> books=Arrays.asList(book);
        when(bookService.findAll()).thenReturn(books);

       List<Book> responseBooks=resources.target("/books").request().get(new GenericType<List<Book>>(){});

       Assert.assertEquals(books.size(),responseBooks.size());
       Assert.assertEquals(books.get(0).getId(),responseBooks.get(0).getId());
       Assert.assertEquals(books.get(0).getName(),responseBooks.get(0).getName());
    }

    @Test
    public void getBookByIdTest(){
        Book book=new Book();
        book.setId(12L);
        book.setName("Name");
        when(bookService.findById(eq(book.getId()))).thenReturn(book);

        Book responseBook=resources.target("/books/"+book.getId()).request().get(Book.class);

        Assert.assertEquals(book.getId(),responseBook.getId());
        Assert.assertEquals(book.getName(),responseBook.getName());
    }

    @Test
    public void getBookByIdTest_bookNotFound(){
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/books/2").request().get().getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addBookTest(){
        Book sendBook=new Book();
        sendBook.setId(12L);
        sendBook.setName("Name");
        ArgumentCaptor<Book> bookCaptor=new ArgumentCaptor<Book>();
        when(bookService.save(bookCaptor.capture())).thenReturn(1L);

        resources.target("/books").request().post(Entity.entity(sendBook,MediaType.APPLICATION_JSON));

        verify(bookService).save(any(Book.class));
        Assert.assertEquals(bookCaptor.getValue().getId(),sendBook.getId());
        Assert.assertEquals(bookCaptor.getValue().getName(),sendBook.getName());
    }

    @Test
    public void updateBookTest(){
        Book sendBook=new Book();
        sendBook.setId(12L);
        sendBook.setName("Name");

        when(bookService.findById(eq(sendBook.getId()))).thenReturn(sendBook);

        Book returnBook=resources.target("/books").request().put(Entity.entity(sendBook,MediaType.APPLICATION_JSON),Book.class);

        verify(bookService).update(any(Book.class));
        Assert.assertEquals(sendBook.getId(),returnBook.getId());
        Assert.assertEquals(sendBook.getName(),returnBook.getName());
    }

    @Test
    public void updateBookTest_bookNotFound(){
        Book sendBook=new Book();
        sendBook.setId(12L);
        sendBook.setName("Name");
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books").request().put(Entity.entity(sendBook,MediaType.APPLICATION_JSON)).getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
        verify(bookService,times(0)).update(any());
    }

    @Test
    public void deleteBookTest(){
        Book deleteBook=new Book();
        deleteBook.setId(12L);
        deleteBook.setName("Name");

        when(bookService.findById(anyLong())).thenReturn(deleteBook);

        Response.StatusType responseStatus= resources.target("/books/"+deleteBook.getId()).request().delete().getStatusInfo();

        Assert.assertEquals(Response.Status.OK,responseStatus);
        verify(bookService).delete(eq(deleteBook.getId()));
    }

    @Test
    public void deleteBookTest_bookNotFound(){
        Book deleteBook=new Book();
        deleteBook.setId(12L);
        deleteBook.setName("Name");
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books/"+deleteBook.getId()).request().delete().getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
        verify(bookService,times(0)).delete(any());
    }

}
