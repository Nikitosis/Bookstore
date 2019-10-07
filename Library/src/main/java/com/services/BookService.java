package com.services;

import com.MainConfig;
import com.amazonaws.services.codecommit.model.FileTooLargeException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import com.crossapi.api.Action;
import com.crossapi.api.UserBookLog;
import com.crossapi.models.Book;
import com.dao.BookDao;
import com.services.storage.AwsStorageService;
import com.services.storage.StoredFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class BookService {

    private BookDao bookDao;
    private MainConfig mainConfig;
    private OktaService oktaService;
    private AwsStorageService awsStorageService;

    @Autowired
    public BookService(BookDao bookDao, MainConfig mainConfig, OktaService oktaService, AwsStorageService awsStorageService) {
        this.bookDao = bookDao;
        this.mainConfig = mainConfig;
        this.oktaService = oktaService;
        this.awsStorageService = awsStorageService;
    }

    public List<Book> findAll(){
        return bookDao.findAll();
    }

    public Book findById(Long id){
        return bookDao.findById(id);
    }

    public void addFileToBook(Book book, StoredFile file) throws IOException, IllegalArgumentException, FileTooLargeException {
        if(!awsStorageService.isAllowedFileType(file.getFileName())){
            throw new IllegalArgumentException("Wrong file type for book");
        }

        Long fileSize=getFileSize(file.getInputStream());
        if(fileSize==-1 || fileSize>mainConfig.getAwsConfig().getMaxFileSize())
            throw new FileTooLargeException("File size if too large or not defined. Max file size is "+mainConfig.getAwsConfig().getMaxFileSize());

        String path=awsStorageService.uploadFile(file, CannedAccessControlList.Private);
        book.setFilePath(path);
        bookDao.update(book);
    }

    public void addImageToBook(Book book,StoredFile file) throws IOException, IllegalArgumentException, FileTooLargeException {
        if(!awsStorageService.isAllowedImageType(file.getFileName())){
            throw new IllegalArgumentException("Wrong image type");
        }

        Long fileSize=getFileSize(file.getInputStream());
        if(fileSize==-1 || fileSize>mainConfig.getAwsConfig().getMaxImageSize())
            throw new FileTooLargeException("Image size if too large or not defined. Max image size is "+mainConfig.getAwsConfig().getMaxImageSize());


        String path=awsStorageService.uploadFile(file, CannedAccessControlList.PublicRead);
        URL url=awsStorageService.getFileUrl(path);
        book.setPhotoLink(url.toString());
        bookDao.update(book);
    }

    public Long save(Book book){
        return bookDao.save(book);
    }

    public void update(Book book){
        bookDao.update(book);
    }

    public void delete(Long id){
        bookDao.delete(id);
    }

    public List<Book> findTakenByUser(Long userId){
        return bookDao.findTakenByUser(userId);
    }

    public StoredFile getStoredFile(Long bookId){
        Book book=bookDao.findById(bookId);
        InputStream inputStream=awsStorageService.getFileInputStream(book.getFilePath());
        String fileName=book.getName()+"."+book.getFilePath().substring(book.getFilePath().lastIndexOf(".")+1);
        return new StoredFile(inputStream,fileName);
    }

    public URL getUrl(Long bookId){
        Book book=bookDao.findById(bookId);
        return awsStorageService.getFileUrl(book.getFilePath());
    }

    public boolean isTakenByUser(Long userId, Long bookId){
        return bookDao.isTakenByUser(userId,bookId);
    }

    public void takeBook(Long userId, Long bookId, LocalDate returnDate){
        bookDao.takeBook(userId,bookId,LocalDate.now(),returnDate);

        try {
            postChargeBookFee(userId, bookId);
        }catch(Exception e){
            e.printStackTrace();
        }

        UserBookLog userBookLog =new UserBookLog();
        userBookLog.setUserId(userId);
        userBookLog.setBookId(bookId);
        userBookLog.setAction(Action.TAKE);
        userBookLog.setDate(LocalDateTime.now());

        try {
            postUserBookLog(userBookLog);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void returnBook(Long userId,Long bookId){
        bookDao.returnBook(userId,bookId);

        UserBookLog userBookLog =new UserBookLog();
        userBookLog.setUserId(userId);
        userBookLog.setBookId(bookId);
        userBookLog.setAction(Action.RETURN);
        userBookLog.setDate(LocalDateTime.now());

        System.out.println(Entity.entity(userBookLog,MediaType.APPLICATION_JSON).toString());

        try {
           postUserBookLog(userBookLog);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Future<Response> postUserBookLog(UserBookLog userBookLog){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();

        Client client = ClientBuilder.newClient();
        return client.target(mainConfig.getClientBookLoggerService().getUrl())
                    .path("/actions")
                    .request(MediaType.APPLICATION_JSON)
                    .header(mainConfig.getSecurity().getTokenHeader(),mainConfig.getSecurity().getTokenPrefix()+accessToken.getValue())
                    .async()
                    .post(Entity.entity(userBookLog, MediaType.APPLICATION_JSON));
    }

    public Future<Response> postChargeBookFee(Long userId,Long bookId){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();

        Client client = ClientBuilder.newClient();
        return client.target(mainConfig.getFeeChargerService().getUrl())
                .path("/users/"+userId+"/books/"+bookId)
                .request(MediaType.APPLICATION_JSON)
                .header(mainConfig.getSecurity().getTokenHeader(),mainConfig.getSecurity().getTokenPrefix()+accessToken.getValue())
                .async()
                .put(Entity.json(""));
    }

    private Long getFileSize(InputStream inputStream){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(inputStream,baos);
        } catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
        return Long.valueOf(baos.size());
    }
}
