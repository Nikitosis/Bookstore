package com.services;

import com.api.Action;
import com.api.ClientBookLog;
import com.dao.BookDao;
import com.models.Book;
import com.models.Client;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceTest {
    @Mock
    private BookDao bookDao;

    @InjectMocks
    private BookService bookService;

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
    public void returnBookTest(){
        ArgumentCaptor<ClientBookLog> logCaptor=new ArgumentCaptor<ClientBookLog>();
        BookService spyBookService=spy(bookService);
        doNothing().when(spyBookService).postClientBookLog(logCaptor.capture());

        spyBookService.returnBook(testClient.getId(),testBook.getId());

        Assert.assertEquals(testClient.getId(),logCaptor.getValue().getClientId());
        Assert.assertEquals(testBook.getId(),logCaptor.getValue().getClientId());
        Assert.assertEquals(Action.RETURN,logCaptor.getValue().getAction());
        verify(bookDao).returnBook(testClient.getId(),testBook.getId());
    }

    @Test
    public void takeBookTest(){
        ArgumentCaptor<ClientBookLog> logCaptor=new ArgumentCaptor<ClientBookLog>();
        BookService spyBookService=spy(bookService);
        doNothing().when(spyBookService).postClientBookLog(logCaptor.capture());

        spyBookService.takeBook(testClient.getId(),testBook.getId());

        Assert.assertEquals(testClient.getId(),logCaptor.getValue().getClientId());
        Assert.assertEquals(testBook.getId(),logCaptor.getValue().getClientId());
        Assert.assertEquals(Action.TAKE,logCaptor.getValue().getAction());
        verify(bookDao).takeBook(testClient.getId(),testBook.getId());
    }
}
