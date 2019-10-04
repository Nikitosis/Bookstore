package com.services;

import com.MainConfig;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import com.crossapi.api.Action;
import com.crossapi.api.UserBookLog;
import com.dao.BookDao;
import com.models.Book;
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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.IllegalFormatException;
import java.util.List;

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

    public void addFileToBook(Book book, StoredFile file) throws IOException, IllegalArgumentException {
        if(!awsStorageService.isAllowedFileType(file.getFileName())){
            throw new IllegalArgumentException("Wrong file type for book");
        }

        String path=awsStorageService.uploadFile(file, CannedAccessControlList.Private);
        book.setUrl(path);
        bookDao.update(book);
    }

    public void addImageToBook(Book book,StoredFile file) throws IOException, IllegalArgumentException {
        if(!awsStorageService.isAllowedImageType(file.getFileName())){
            throw new IllegalArgumentException("Wrong image type");
        }

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
        InputStream inputStream=awsStorageService.getFileInputStream(book.getUrl());
        String fileName=book.getName()+"."+book.getUrl().substring(book.getUrl().lastIndexOf(".")+1);
        return new StoredFile(inputStream,fileName);
    }

    public URL getUrl(Long bookId){
        Book book=bookDao.findById(bookId);
        return awsStorageService.getFileUrl(book.getUrl());
    }

    public boolean isTakenByUser(Long userId, Long bookId){
        return bookDao.isTakenByUser(userId,bookId);
    }

    public void takeBook(Long userId, Long bookId, LocalDate returnDate){
        bookDao.takeBook(userId,bookId,LocalDate.now(),returnDate);

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

    public void postUserBookLog(UserBookLog userBookLog){
        OAuth2AccessToken accessToken=oktaService.getOktaToken();

        Client client = ClientBuilder.newClient();
        Response response=client.target(mainConfig.getClientBookLoggerService().getUrl())
                    .path("/actions")
                    .request(MediaType.APPLICATION_JSON)
                    .header(mainConfig.getSecurity().getTokenHeader(),mainConfig.getSecurity().getTokenPrefix()+accessToken.getValue())
                    .post(Entity.entity(userBookLog, MediaType.APPLICATION_JSON));
    }
}
