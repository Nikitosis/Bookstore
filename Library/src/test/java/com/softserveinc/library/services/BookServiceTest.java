package com.softserveinc.library.services;

import com.softserveinc.library.MainConfig;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.library.dao.BookDao;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.library.services.storage.AwsStorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    @Mock
    private MainConfig mainConfig;
    @Mock
    private RequestSenderService requestSenderService;
    @Mock
    private AwsStorageService awsStorageService;

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

        bookService.takeBook(testUser.getId(),testBook.getId(), LocalDate.now());

        verify(requestSenderService).postUserBookLog(any());
        verify(requestSenderService).postChargeBookFee(eq(testUser.getId()),eq(testBook.getId()));
        verify(bookDao).takeBook(eq(testUser.getId()),eq(testBook.getId()),any(),any());
    }

    @Test
    public void returnBookTest(){

        bookService.returnBook(testUser.getId(),testBook.getId());

        verify(requestSenderService).postUserBookLog(any());
        verify(bookDao).returnBook(eq(testUser.getId()),eq(testBook.getId()));
    }
}
