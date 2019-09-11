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

    private Book testBook;

    @Before
    public void init(){
        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
    }


    @Test
    public void getBooksTest(){
        List<Book> books=Arrays.asList(testBook);
        when(bookService.findAll()).thenReturn(books);

       List<Book> responseBooks=resources.target("/books")
               .request()
               .get(new GenericType<List<Book>>(){});

       Assert.assertArrayEquals(books.toArray(),responseBooks.toArray());
    }

    @Test
    public void getBookByIdTest(){
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);

        Book responseBook=resources.target("/books/"+testBook.getId())
                .request()
                .get(Book.class);

        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void getBookByIdTest_bookNotFound(){
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/books/2")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addBookTest(){
        when(bookService.save(any(Book.class))).thenReturn(1L);

        Book responseBook=resources.target("/books")
                .request()
                .post(Entity.entity(testBook,MediaType.APPLICATION_JSON),Book.class);

        verify(bookService).save(eq(testBook));
        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void updateBookTest(){
        when(bookService.findById(eq(testBook.getId()))).thenReturn(testBook);

        Book responseBook=resources.target("/books")
                .request()
                .put(Entity.entity(testBook,MediaType.APPLICATION_JSON),Book.class);

        verify(bookService).update(eq(testBook));
        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void updateBookTest_bookNotFound(){
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books")
                .request()
                .put(Entity.entity(testBook,MediaType.APPLICATION_JSON)).getStatusInfo();

        verify(bookService,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void deleteBookTest(){
        when(bookService.findById(anyLong())).thenReturn(testBook);

        Response.StatusType responseStatus= resources.target("/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookService).delete(eq(testBook.getId()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteBookTest_bookNotFound(){
        when(bookService.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookService,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

}
