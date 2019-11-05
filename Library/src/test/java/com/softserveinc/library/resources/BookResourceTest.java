package com.softserveinc.library.resources;

import com.softserveinc.library.MainConfig;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.cross_api_objects.mixins.MixinModule;
import com.softserveinc.library.dao.BookDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserveinc.library.services.BookService;
import com.softserveinc.library.services.request_senders.RequestSenderHttpService;
import com.softserveinc.library.services.UserService;
import com.softserveinc.library.services.request_senders.RequestSenderKafkaService;
import com.softserveinc.library.services.storage.AwsStorageService;
import com.softserveinc.library.services.storage.StoredFile;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Validator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Integration test for BookResource
 */
@RunWith(MockitoJUnitRunner.class)
public class BookResourceTest {
    //Building MainConfig
    final ObjectMapper objectMapper = Jackson.newObjectMapper().registerModule(new MixinModule());
    final Validator validator = Validators.newValidator();
    final YamlConfigurationFactory<MainConfig> factory = new YamlConfigurationFactory<>(MainConfig.class,validator,objectMapper,"dw");
    final File yaml=new File(Thread.currentThread().getContextClassLoader().getResource("test-configuration.yml").getPath());

    //Creating mocks
    private BookDao bookDao=mock(BookDao.class);
    final MainConfig configuration=factory.build(yaml);
    private Authentication authentication=mock(Authentication.class);

    //Creating dependencies
    private AwsStorageService awsStorageService=mock(AwsStorageService.class);
    private UserService userService=mock(UserService.class);
    private RequestSenderHttpService requestSenderHttpService =mock(RequestSenderHttpService.class);
    private RequestSenderKafkaService requestSenderKafkaService=mock(RequestSenderKafkaService.class);
    private BookService bookService=new BookService(bookDao,configuration, requestSenderHttpService,requestSenderKafkaService,awsStorageService,userService);

    //Creating ResourceTestRule
    @Rule
    public ResourceTestRule resources=ResourceTestRule.builder()
            .addResource(new BookResource(bookService))
            .addResource(new MultiPartFeature())
            .setMapper(objectMapper)
            .build();

    //Test entities
    private Book testBook;
    FileDataBodyPart testFilePart;
    FileDataBodyPart testImagePart;
    FormDataBodyPart testBookPart;


    public BookResourceTest() throws IOException, ConfigurationException {
    }

    @Before
    public void init() {

        testBook=new Book();
        testBook.setId(12L);
        testBook.setName("Name");
        testBook.setFilePath("testFile.pdf");
        testBook.setPhotoLink("http://someling.png");
        testBook.setIsbn("12521234");
        testBook.setPrice(new BigDecimal("12.99"));

        testFilePart=new FileDataBodyPart("file",new File("src/test/resources/testFile.pdf"));
        testImagePart=new FileDataBodyPart("image",new File("src/test/resources/testImage.png"));
        testBookPart=new FormDataBodyPart("bookInfo",testBook,MediaType.APPLICATION_JSON_TYPE);

        //building security mocks
        SecurityContext securityContext=mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    public void getBooksTest(){
        List<Book> books=Arrays.asList(testBook);
        when(bookDao.findAll()).thenReturn(books);

       List<Book> responseBooks=resources.target("/books")
               .request()
               .get(new GenericType<List<Book>>(){});

       Assert.assertArrayEquals(books.toArray(),responseBooks.toArray());
    }

    @Test
    public void getBookByIdTest(){
        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);

        Book responseBook=resources.target("/books/"+testBook.getId())
                .request()
                .get(Book.class);

        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void getBookByIdTest_bookNotFound(){
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/books/2")
                .request()
                .get()
                .getStatusInfo();

        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void addBookTest() throws IOException, ParseException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testBookPart)
                .bodyPart(testFilePart)
                .bodyPart(testImagePart);

        when(bookDao.save(any(Book.class))).thenReturn(1L);
        when(awsStorageService.isAllowedImageType(any())).thenReturn(true);
        when(awsStorageService.isAllowedFileType(any())).thenReturn(true);
        //when uploading file
        when(awsStorageService.uploadFile(
                    argThat((StoredFile file)-> testFilePart.getFileEntity().getName().equals(file.getFileName())),
                    eq(CannedAccessControlList.Private))
            ).thenReturn(testBook.getFilePath());
        //when getting photo url
        when(awsStorageService.getFileUrl(any())).thenReturn(testBook.getPhotoLink());

        Book responseBook=resources.target("/books")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA),Book.class);

        verify(bookDao).save(eq(testBook));
        //upload image with public access
        verify(awsStorageService).uploadFile(
                argThat((StoredFile image)-> testImagePart.getFileEntity().getName().equals(image.getFileName())),
                eq(CannedAccessControlList.PublicRead)
        );
        //upload file with private access
        verify(awsStorageService).uploadFile(
                argThat((StoredFile file)-> testFilePart.getFileEntity().getName().equals(file.getFileName())),
                eq(CannedAccessControlList.Private)
        );

        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void addBookTest_invalidBookInfo(){
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testFilePart)
                .bodyPart(testImagePart);

        int responseStatus=resources.target("/books")
                .register(MultiPartFeature.class)
                .request()
                .post(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatus();

        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422,responseStatus);
    }

    @Test
    public void updateBookTest() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testBookPart)
                .bodyPart(testFilePart)
                .bodyPart(testImagePart);

        when(bookDao.findById(eq(testBook.getId()))).thenReturn(testBook);
        when(awsStorageService.isAllowedImageType(any())).thenReturn(true);
        when(awsStorageService.isAllowedFileType(any())).thenReturn(true);
        //when uploading file
        when(awsStorageService.uploadFile(
                argThat((StoredFile file)-> testFilePart.getFileEntity().getName().equals(file.getFileName())),
                eq(CannedAccessControlList.Private))
        ).thenReturn(testBook.getFilePath());
        //when getting photo url
        when(awsStorageService.getFileUrl(any())).thenReturn(testBook.getPhotoLink());

        Book responseBook=resources.target("/books")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA),Book.class);

        verify(bookDao,atLeast(1)).update(any());
        //upload image with public access
        verify(awsStorageService).uploadFile(
                argThat((StoredFile image)-> testImagePart.getFileEntity().getName().equals(image.getFileName())),
                eq(CannedAccessControlList.PublicRead)
        );
        //upload file with private access
        verify(awsStorageService).uploadFile(
                argThat((StoredFile file)-> testFilePart.getFileEntity().getName().equals(file.getFileName())),
                eq(CannedAccessControlList.Private)
        );
        Assert.assertEquals(testBook,responseBook);
    }

    @Test
    public void updateBook_invalidBookInfo(){
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testFilePart)
                .bodyPart(testImagePart);

        int responseStatus=resources.target("/books")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatus();

        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY_422,responseStatus);
    }

    @Test
    public void updateBookTest_bookNotFound(){
        when(bookDao.findById(anyLong())).thenReturn(null);

        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testBookPart);
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA)).getStatusInfo();

        verify(bookDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void setBookFileTest() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testFilePart);

        when(bookDao.findById(anyLong())).thenReturn(testBook);
        when(awsStorageService.isAllowedFileType(any())).thenReturn(true);

        Book responseBook=resources.target("/books")
                .path(testBook.getId()+"/file")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA),Book.class);

        verify(bookDao,atLeast(1)).update(eq(testBook));
        Assert.assertEquals(responseBook,testBook);
        //upload file with private access
        verify(awsStorageService).uploadFile(
                argThat((StoredFile file)-> testFilePart.getFileEntity().getName().equals(file.getFileName())),
                eq(CannedAccessControlList.Private)
        );
    }

    @Test
    public void setBookFileTest_bookNotFound() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testFilePart);

        Response.StatusType responseStatus=resources.target("/books")
                .path(testBook.getId()+"/file")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();


        verify(bookDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void setBookFileTest_wrongFile() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testFilePart);

        when(bookDao.findById(anyLong())).thenReturn(testBook);
        when(awsStorageService.isAllowedFileType(any())).thenReturn(false);

        Response.StatusType responseStatus=resources.target("/books")
                .path(testBook.getId()+"/file")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();


        verify(bookDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }

    @Test
    public void setBookImageTest() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testImagePart);

        when(bookDao.findById(anyLong())).thenReturn(testBook);
        when(awsStorageService.isAllowedImageType(any())).thenReturn(true);

        Book responseBook=resources.target("/books")
                .path(testBook.getId()+"/image")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA),Book.class);

        verify(bookDao,atLeast(1)).update(eq(testBook));
        Assert.assertEquals(responseBook,testBook);
        //upload file with private access
        verify(awsStorageService).uploadFile(
                argThat((StoredFile image)-> testImagePart.getFileEntity().getName().equals(image.getFileName())),
                eq(CannedAccessControlList.PublicRead)
        );
    }

    @Test
    public void setBookImageTest_bookNotFound() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testImagePart);

        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus=resources.target("/books")
                .path(testBook.getId()+"/image")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();


        verify(bookDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

    @Test
    public void setBookImageTest_wrongFile() throws IOException {
        FormDataMultiPart multiPart=new FormDataMultiPart();
        multiPart
                .bodyPart(testImagePart);

        when(bookDao.findById(anyLong())).thenReturn(testBook);

        Response.StatusType responseStatus=resources.target("/books")
                .path(testBook.getId()+"/image")
                .register(MultiPartFeature.class)
                .request()
                .put(Entity.entity(multiPart,MediaType.MULTIPART_FORM_DATA))
                .getStatusInfo();


        verify(bookDao,times(0)).update(any());
        Assert.assertEquals(Response.Status.BAD_REQUEST,responseStatus);
    }
    @Test
    public void deleteBookTest(){
        when(bookDao.findById(anyLong())).thenReturn(testBook);

        Response.StatusType responseStatus= resources.target("/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao).delete(eq(testBook.getId()));
        Assert.assertEquals(Response.Status.OK,responseStatus);
    }

    @Test
    public void deleteBookTest_bookNotFound(){
        when(bookDao.findById(anyLong())).thenReturn(null);

        Response.StatusType responseStatus= resources.target("/books/"+testBook.getId())
                .request()
                .delete()
                .getStatusInfo();

        verify(bookDao,times(0)).delete(any());
        Assert.assertEquals(Response.Status.NOT_FOUND,responseStatus);
    }

}
