package com.services;

import com.api.Action;
import com.api.UserBookLog;
import com.dao.BookDao;
import com.models.Book;
import com.models.User;
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

    private User testUser;
    private Book testBook;

    @Before
    public void init(){
        testUser =new User();
        testUser.setUsername("username");
        testUser.setPassword("password");
        testUser.setfName("FNAME");
        testUser.setlName("LNAME");

        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
        testBook.setTaken(false);
    }

    @Test
    public void returnBookTest(){
        ArgumentCaptor<UserBookLog> logCaptor=new ArgumentCaptor<UserBookLog>();
        BookService spyBookService=spy(bookService);
        doNothing().when(spyBookService).postUserBookLog(logCaptor.capture());

        spyBookService.returnBook(testUser.getId(),testBook.getId());

        Assert.assertEquals(testUser.getId(),logCaptor.getValue().getUserId());
        Assert.assertEquals(testBook.getId(),logCaptor.getValue().getBookId());
        Assert.assertEquals(Action.RETURN,logCaptor.getValue().getAction());
        verify(bookDao).returnBook(eq(testUser.getId()),eq(testBook.getId()));
    }

    @Test
    public void takeBookTest(){
        ArgumentCaptor<UserBookLog> logCaptor=new ArgumentCaptor<UserBookLog>();
        BookService spyBookService=spy(bookService);
        doNothing().when(spyBookService).postUserBookLog(logCaptor.capture());

        spyBookService.takeBook(testUser.getId(),testBook.getId());

        Assert.assertEquals(testUser.getId(),logCaptor.getValue().getUserId());
        Assert.assertEquals(testBook.getId(),logCaptor.getValue().getBookId());
        Assert.assertEquals(Action.TAKE,logCaptor.getValue().getAction());
        verify(bookDao).takeBook(eq(testUser.getId()),eq(testBook.getId()));
    }
}
