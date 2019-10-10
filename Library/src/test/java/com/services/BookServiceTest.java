package com.services;

import com.crossapi.api.Action;
import com.crossapi.api.UserBookLog;
import com.crossapi.models.Book;
import com.dao.BookDao;
import com.crossapi.models.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        testUser.setId(12L);
        testUser.setPassword("password");
        testUser.setUsername("username");
        testUser.setfName("FNAME");
        testUser.setlName("LNAME");
        testUser.setCity("city");
        testUser.setCountry("country");
        testUser.setGender(User.Gender.FEMALE);
        testUser.setPhone("932312");
        testUser.setEmail("email@gmail.com");
        testUser.setMoney(new BigDecimal("100.01"));
        testUser.setAvatarLink("http://someUserLink.png");

        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
        testBook.setFilePath("testFile.pdf");
        testBook.setPhotoLink("http://someBookLink.png");
        testBook.setIsbn("12521234");
        testBook.setPrice(new BigDecimal("12.99"));
    }

    @Test
    public void takeBookTest(){
        ArgumentCaptor<UserBookLog> logCaptor=ArgumentCaptor.forClass(UserBookLog.class);
        BookService spyBookService=spy(bookService);
        doReturn(null).when(spyBookService).postUserBookLog(logCaptor.capture());
        doReturn(null).when(spyBookService).postChargeBookFee(eq(testUser.getId()),eq(testBook.getId()));

        spyBookService.takeBook(testUser.getId(),testBook.getId(), LocalDate.now());

        Assert.assertEquals(testUser.getId(),logCaptor.getValue().getUserId());
        Assert.assertEquals(testBook.getId(),logCaptor.getValue().getBookId());
        Assert.assertEquals(Action.TAKE,logCaptor.getValue().getAction());
        verify(bookDao).takeBook(eq(testUser.getId()),eq(testBook.getId()),any(),any());
    }

    @Test
    public void returnBookTest(){
        ArgumentCaptor<UserBookLog> logCaptor=ArgumentCaptor.forClass(UserBookLog.class);
        BookService spyBookService=spy(bookService);
        doReturn(null).when(spyBookService).postUserBookLog(logCaptor.capture());

        spyBookService.returnBook(testUser.getId(),testBook.getId());

        Assert.assertEquals(testUser.getId(),logCaptor.getValue().getUserId());
        Assert.assertEquals(testBook.getId(),logCaptor.getValue().getBookId());
        Assert.assertEquals(Action.RETURN,logCaptor.getValue().getAction());
        verify(bookDao).returnBook(eq(testUser.getId()),eq(testBook.getId()));
    }
}
