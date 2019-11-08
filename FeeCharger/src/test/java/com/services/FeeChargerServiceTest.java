package com.services;

import com.softserveinc.feecharger.configuration.FeeChargeConfig;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.cross_api_objects.services.OktaService;
import com.softserveinc.feecharger.MainConfig;
import com.softserveinc.feecharger.dao.BookDao;
import com.softserveinc.feecharger.dao.FeeChargerDao;
import com.softserveinc.feecharger.dao.UserDao;
import com.softserveinc.feecharger.models.UserBook;
import com.softserveinc.feecharger.services.FeeChargerService;
import com.softserveinc.feecharger.services.request_senders.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeeChargerServiceTest {
    @Mock
    private BookDao bookDao;

    @Mock
    private FeeChargerDao feeChargerDao;

    @Mock
    private UserDao userDao;

    @Mock
    private MainConfig mainConfig;

    @Mock
    private OktaService oktaService;

    @Mock
    private RequestSenderHttpService requestSenderHttpService;

    @Mock
    private RequestSenderKafkaService requestSenderKafkaService;

    @InjectMocks
    private FeeChargerService feeChargerService;

    private User testUser;
    private Book testBook;
    private UserBook testRent;


    public FeeChargerServiceTest(){
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
        testBook.setPhotoLink("http://someling.png");
        testBook.setIsbn("12521234");
        testBook.setPrice(new BigDecimal("12.99"));

        testRent=new UserBook();
        testRent.setBookId(testBook.getId());
        testRent.setUserId(testUser.getId());
    }

//    @Before
//    public void defaultMocks(){
//        DependencyService mailSenderService=mock(DependencyService.class);
//        when(mainConfig.getMailSenderService()).thenReturn(mailSenderService);
//        when(mailSenderService.getUrl()).thenReturn("mailSenderUrl");
//
//        DependencyService libraryService=mock(DependencyService.class);
//        when(mainConfig.getLibraryService()).thenReturn(libraryService);
//        when(libraryService.getUrl()).thenReturn("libraryServiceUrl");
//
//        DependencyService loggerService=mock(DependencyService.class);
//        when(mainConfig.getLoggerService()).thenReturn(loggerService);
//        when(loggerService.getUrl()).thenReturn("loggerServiceUrl");
//
//        FeeChargeConfig feeChargeConfig=mock(FeeChargeConfig.class);
//        when(mainConfig.getFeeChargeConfig()).thenReturn(feeChargeConfig);
//        when(feeChargeConfig.getRentPeriod()).thenReturn(10000000L);
//    }

    @Test
    public void tryExtendRentTest_rentIsValid(){
        testRent.setPaidUntil(LocalDateTime.now().plusMonths(1));

        feeChargerService.tryExtendRent(testRent);

        verify(feeChargerDao,times(0)).chargeFee(any(),any());
        verify(feeChargerDao,times(0)).extendBookRent(any(),any(),any());
    }

    @Test
    public void tryExtendRentTest_notEnoughMoney_returnBook() throws Exception {
        testUser.setMoney(new BigDecimal("1"));
        testBook.setPrice(new BigDecimal("10"));
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        feeChargerService.tryExtendRent(testRent);

        verify(feeChargerDao,times(0)).chargeFee(any(),any());
        verify(feeChargerDao,times(0)).extendBookRent(any(),any(),any());
    }

    @Test
    public void tryExtendRentTest_extendBook(){
        FeeChargeConfig feeChargeConfig=mock(FeeChargeConfig.class);
        when(mainConfig.getFeeChargeConfig()).thenReturn(feeChargeConfig);
        when(feeChargeConfig.getRentPeriod()).thenReturn(10000000L);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        feeChargerService.tryExtendRent(testRent);

        verify(feeChargerDao).chargeFee(eq(testUser.getId()),eq(testBook.getPrice()));
        verify(feeChargerDao).extendBookRent(eq(testUser.getId()),eq(testBook.getId()),any());
    }

}
