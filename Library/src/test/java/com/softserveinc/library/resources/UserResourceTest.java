package com.softserveinc.library.resources;

import com.amazonaws.util.Base64;
import com.softserveinc.library.MainConfig;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.mixins.MixinModule;
import com.softserveinc.library.dao.BookDao;
import com.softserveinc.cross_api_objects.dao.RoleDao;
import com.softserveinc.cross_api_objects.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserveinc.cross_api_objects.models.Role;
import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.library.services.BookService;
import com.softserveinc.library.services.request_senders.RequestSenderHttpService;
import com.softserveinc.library.services.UserService;
import com.softserveinc.library.services.request_senders.RequestSenderKafkaService;
import com.softserveinc.cross_api_objects.services.storage.AwsStorageService;
import com.softserveinc.cross_api_objects.services.storage.StoredFile;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Validator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Integration test for BookResource
 */
@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {
    //Building MainConfig
    private final ObjectMapper objectMapper = Jackson.newObjectMapper().registerModule(new MixinModule());
    private final Validator validator = Validators.newValidator();
    private final YamlConfigurationFactory<MainConfig> factory = new YamlConfigurationFactory<>(MainConfig.class,validator,objectMapper,"dw");
    private final File yaml=new File(Thread.currentThread().getContextClassLoader().getResource("test-configuration.yml").getPath());
    private final MainConfig configuration=factory.build(yaml);

    private final PasswordEncoder passwordEncoder=mock(PasswordEncoder.class);

    //Creating mocks
    private UserDao userDao =mock(UserDao.class);
    private BookDao bookDao=mock(BookDao.class);
    private RoleDao roleDao=mock(RoleDao.class);
    private Authentication authentication=mock(Authentication.class);

    //Creating dependencies
    private AwsStorageService awsStorageService=mock(AwsStorageService.class);

    private RequestSenderHttpService requestSenderHttpService =mock(RequestSenderHttpService.class);
    private RequestSenderKafkaService requestSenderKafkaService=mock(RequestSenderKafkaService.class);
    private UserService userService =new UserService(userDao,roleDao,awsStorageService, requestSenderKafkaService,passwordEncoder,configuration);
    private BookService bookService=spy(new BookService(bookDao,configuration, requestSenderKafkaService,awsStorageService,userService));

    //Creating ResourceTestRule
    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new UserResource(userService,bookService,roleDao))
            .addResource(new MultiPartFeature())
            .setMapper(objectMapper)
            .build();

    //Test entities
    private User testUser;
    private User anotherTestUser;
    private Book testBook;
    FileDataBodyPart testFilePart;
    FileDataBodyPart testImagePart;
    FormDataBodyPart testUserPart;

    UUID uuid;

    public UserResourceTest() throws IOException, ConfigurationException {
    }

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
        testUser.setEmailVerified(false);
        testUser.setVerificationToken(UUID.randomUUID().toString());

        anotherTestUser =new User();
        anotherTestUser.setId(13L);
        anotherTestUser.setPassword("password1");
        anotherTestUser.setUsername("username1");
        anotherTestUser.setfName("FNAME1");
        anotherTestUser.setlName("LNAME1");
        anotherTestUser.setCity("city1");
        anotherTestUser.setCountry("country1");
        anotherTestUser.setGender(User.Gender.MALE);
        anotherTestUser.setPhone("9323121");
        anotherTestUser.setEmail("email@gmail.com1");
        anotherTestUser.setMoney(new BigDecimal("100.1"));
        anotherTestUser.setAvatarLink("http://someUserLink1.png");
        anotherTestUser.setEmailVerified(false);
        anotherTestUser.setVerificationToken(UUID.randomUUID().toString());

        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
        testBook.setFilePath("testFile.pdf");
        testBook.setPhotoLink("http://someBookLink.png");
        testBook.setIsbn("12521234");
        testBook.setPrice(new BigDecimal("12.99"));

        testFilePart=new FileDataBodyPart("file",new File("src/test/resources/testFile.pdf"));
        testImagePart=new FileDataBodyPart("image",new File("src/test/resources/testImage.png"));
        testUserPart=new FormDataBodyPart("userInfo",testUser,MediaType.APPLICATION_JSON_TYPE);

        //building security mocks
        SecurityContext securityContext=mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


    }

    @Test
    public void getUsersTest(){
        List<User> users= Arrays.asList(testUser);
        when(userDao.findAll()).thenReturn(users);

        List<User> responseUsers =resources.target("/users")
                .request()
                .get(new GenericType<List<User>>(){});

        Assert.assertArrayEquals(users.toArray(), responseUsers.toArray());
    }

    @Test
    public void getUserByIdTest(){
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);

        User responseUser =resources.target("/users/"+ testUser.getId())
                .request()
                .get(User.class);

        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void getUserByIdTest_userNotFound(){
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void setUserImage() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(userService.findById(testUser.getId())).thenReturn(testUser);
        when(awsStorageService.isAllowedImageType(any())).thenReturn(true);
        when(awsStorageService.getFileUrl(any())).thenReturn(testUser.getAvatarLink());

        User responseUser=resources.target("/users/"+testUser.getId()+"/image")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA),User.class);

        Assert.assertEquals(testUser,responseUser);
        verify(userDao,atLeast(1)).update(any());
        verify(awsStorageService).uploadFile(
                argThat((StoredFile image)-> testImagePart.getFileEntity().getName().equals(image.getFileName())),
                eq(CannedAccessControlList.PublicRead)
        );
    }

    @Test
    public void setUserImage_wrongUser(){
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userDao.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/image")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();

        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
        verify(userDao,times(0)).update(any());
    }

    @Test
    public void setUserImage_userNotFound(){
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(userDao.findById(eq(testUser.getId()))).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/image")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
        verify(userDao,times(0)).update(any());
    }

    @Test
    public void setUserImage_wrongImage(){
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/image")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();

        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
        verify(userDao,times(0)).update(any());
    }

    @Test
    public void addUserTest() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(userDao.save(any(User.class))).thenReturn(1L);
        when(awsStorageService.isAllowedImageType(any())).thenReturn(true);
        when(passwordEncoder.encode(eq(testUser.getPassword()))).thenReturn(testUser.getPassword());
        when(awsStorageService.getFileUrl(any())).thenReturn(testUser.getAvatarLink());



        User responseUser =resources.target("/users")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA), User.class);
        responseUser.setVerificationToken(testUser.getVerificationToken());

        verify(userDao).save(any());
        verify(roleDao).addUserRole(eq(testUser.getId()),eq("USER"));
        verify(passwordEncoder).encode(eq(testUser.getPassword()));
        verify(awsStorageService).uploadFile(
                argThat((StoredFile image)-> testImagePart.getFileEntity().getName().equals(image.getFileName())),
                eq(CannedAccessControlList.PublicRead)
        );
        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void addUserTest_invalidUserInfo() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testImagePart);

        int responseStatus =resources.target("/users")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA))
                .getStatus();


        verify(userDao,times(0)).save(eq(testUser));
        verify(awsStorageService,times(0)).uploadFile(any(),any());
        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422,responseStatus);
    }

    @Test
    public void addUserTest_usernameAlreadyTaken() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus =resources.target("/users")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();


        verify(userDao,times(0)).save(eq(testUser));
        verify(awsStorageService,times(0)).uploadFile(any(),any());
        Assert.assertEquals(Response.Status.CONFLICT,responseStatus);
    }

    @Test
    public void updateUserTest() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(awsStorageService.isAllowedImageType(any())).thenReturn(true);

        User responseUser = resources.target("/users")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA), User.class);

        verify(userDao,atLeast(1)).update(any());
        verify(awsStorageService).uploadFile(
                argThat((StoredFile image)-> testImagePart.getFileEntity().getName().equals(image.getFileName())),
                eq(CannedAccessControlList.PublicRead)
        );
        Assert.assertEquals(testUser, responseUser);
    }

    @Test
    public void updateUserTest_invalidUserInfo(){
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testImagePart);

        int responseStatus = resources.target("/users")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatus();

        verify(userDao,times(0)).update(any());
        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422,responseStatus);
    }

    @Test
    public void updateUserTest_wrongUser(){
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userDao.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        Response.StatusType responseStatus = resources.target("/users")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();

        verify(userDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void updateUserTest_userNotFound() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testUserPart)
                .bodyPart(testImagePart);

        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();

        verify(userDao,times(0)).update(any());
        verify(awsStorageService,times(0)).uploadFile(any(),any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getBookTest() throws IOException {
        FileInputStream fileInputStream=new FileInputStream(testFilePart.getFileEntity().getPath());

        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);
        when(userDao.findById(testUser.getId())).thenReturn(testUser);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(true);
        when(awsStorageService.getFileInputStream(eq(testBook.getFilePath()))).thenReturn(fileInputStream);

        ByteArrayInputStream resposeInputStream= (ByteArrayInputStream) resources.target("/users/"+testUser.getId()+"/books/"+testBook.getId())
                .request()
                .get()
                .getEntity();

        byte[] bytes = IOUtils.toByteArray(new FileInputStream(testFilePart.getFileEntity().getPath()));
        byte[] encoded = Base64.encode(bytes);
        Assert.assertArrayEquals(
                encoded,
                IOUtils.toByteArray(resposeInputStream)
        );
    }

    @Test
    public void getBookTest_wrongUser(){
        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userDao.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/books/"+testBook.getId())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void getBookTest_userNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/books/"+testBook.getId())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getBookTest_bookNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/books/"+testBook.getId())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getBookTest_bookNotTaken(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);
        when(userDao.findById(testUser.getId())).thenReturn(testUser);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(false);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/books/"+testBook.getId())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void getBookTest_cannotLoadBook(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userDao.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);
        when(userDao.findById(testUser.getId())).thenReturn(testUser);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(true);
        when(awsStorageService.getFileInputStream(eq(testBook.getFilePath()))).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId()+"/books/"+testBook.getId())
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR,responseStatus);
    }


    @Test
    public void deleteUserTest(){
        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(userDao).delete(eq(testUser.getId()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteUserTest_userNotFound(){
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+testUser.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(userDao,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void getTakenUsersBooksTest(){
        List<Book> testBooks=Arrays.asList(testBook);
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findTakenByUser(eq(testUser.getId()))).thenReturn(testBooks);

        List<Book> responseBooks=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get(new GenericType<List<Book>>(){});

        Assert.assertArrayEquals(testBooks.toArray(),responseBooks.toArray());
    }

    @Test
    public void getTakenUsersBooksTest_wrongUser(){
        List<Book> testBooks=Arrays.asList(testBook);
        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void getTakenUsersBooksTest_wrongUserButAdmin(){
        List<Book> testBooks=Arrays.asList(testBook);

        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);
        when(roleDao.findByUser(anotherTestUser.getId())).thenReturn(Arrays.asList(new Role(1L,"ADMIN")));

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findTakenByUser(eq(testUser.getId()))).thenReturn(testBooks);

        List<Book> responseBooks=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get(new GenericType<List<Book>>(){});

        Assert.assertArrayEquals(testBooks.toArray(),responseBooks.toArray());
    }

    @Test
    public void getTakenUserBooksTest_userNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);
        when(userDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);



        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .queryParam("returnDate","2019-10-02")
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(requestSenderHttpService).sendUserBookLog(any());
        verify(requestSenderHttpService).postChargeBookFee(eq(testUser.getId()),eq(testBook.getId()));
        verify(bookDao).takeBook(eq(testUser.getId()),eq(testBook.getId()),any(),any());
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void takeBookTest_invalidReturnDate(){

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .queryParam("returnDate","2019-1sd0-02")
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookService,times(0)).takeBook(any(),any(),any());
        verify(bookDao,times(0)).takeBook(any(),any(),any(),any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void takeBookTest_wrongUser(){
        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        Response.StatusType responseStatus = resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
        verify(bookService,times(0)).takeBook(any(),any(),any());
    }

    @Test
    public void takeBookTest_userNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookService,times(0)).takeBook(any(),any(),any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBookTest_bookNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookService,times(0)).takeBook(any(),any(),any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void takeBootTest_bookAlreadyTaken(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(true);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();

        verify(bookService,times(0)).takeBook(any(),any(),any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void takeBookTest_notEnoughMoney(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(false);
        testUser.setMoney(new BigDecimal("0"));
        testBook.setPrice(new BigDecimal("10"));

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .put(Entity.json(""))
                .getStatusInfo();


        verify(bookService,times(0)).takeBook(any(),any(),any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void returnBookTest(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(true);

        resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete();

        verify(requestSenderHttpService).sendUserBookLog(any());
        verify(bookDao).returnBook(eq(testUser.getId()),eq(testBook.getId()));
    }

    @Test
    public void returnBookTest_wrongUser(){
        when(authentication.getName()).thenReturn(anotherTestUser.getUsername());
        when(userService.findByUsername(eq(anotherTestUser.getUsername()))).thenReturn(anotherTestUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(eq(testUser.getId()),eq(testBook.getId()));
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void returnBookTest_userNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBookTest_bookNotFound(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void returnBootTest_bookNotTaken(){
        when(authentication.getName()).thenReturn(testUser.getUsername());
        when(userService.findByUsername(eq(testUser.getUsername()))).thenReturn(testUser);

        when(userDao.findById(eq(testUser.getId()))).thenReturn(testUser);
        when(bookDao.findByIdWithUser(eq(testBook.getId()),eq(testUser.getId()))).thenReturn(testBook);
        when(bookDao.isTakenByUser(eq(testUser.getId()),eq(testBook.getId()))).thenReturn(false);

        Response.StatusType responseStatus=resources.target("/users/"+ testUser.getId()+"/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).returnBook(anyLong(),anyLong());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }
}
