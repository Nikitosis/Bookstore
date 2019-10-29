package com.softserveinc.library.services;

import com.softserveinc.cross_api_objects.models.User;
import com.softserveinc.library.MainConfig;
import com.amazonaws.services.codecommit.model.FileTooLargeException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.util.IOUtils;
import com.softserveinc.cross_api_objects.api.Action;
import com.softserveinc.cross_api_objects.api.UserBookLog;
import com.softserveinc.cross_api_objects.models.Book;
import com.softserveinc.library.dao.BookDao;
import com.softserveinc.library.services.storage.AwsStorageService;
import com.softserveinc.library.services.storage.StoredFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookService {

    private BookDao bookDao;
    private MainConfig mainConfig;
    private RequestSenderService requestSenderService;
    private AwsStorageService awsStorageService;
    private UserService userService;

    @Autowired
    public BookService(BookDao bookDao, MainConfig mainConfig, RequestSenderService requestSenderService, AwsStorageService awsStorageService, UserService userService) {
        this.bookDao = bookDao;
        this.mainConfig = mainConfig;
        this.requestSenderService = requestSenderService;
        this.awsStorageService = awsStorageService;
        this.userService = userService;
    }

    public List<Book> findAll(){
        User authUser=getAuthenticatedUser();
        if(authUser==null)
            return bookDao.findAll();
        else
            return bookDao.findAllWithUser(authUser.getId());
    }

    public Book findById(Long id){
        User authUser=getAuthenticatedUser();
        if(authUser==null)
            return bookDao.findById(id);
        else
            return bookDao.findByIdWithUser(id,authUser.getId());
    }

    public void addFileToBook(Book book, StoredFile file) throws IOException, IllegalArgumentException, FileTooLargeException {
        Long fileSize=getFileSize(file);
        if(fileSize<=4 || fileSize>mainConfig.getAwsS3Config().getMaxFileSize())
            throw new FileTooLargeException("File size if too large or not defined. Max file size is "+mainConfig.getAwsS3Config().getMaxFileSize());


        if(!awsStorageService.isAllowedFileType(file.getFileName())){
            throw new IllegalArgumentException("Wrong file type for book");
        }
        String path=awsStorageService.uploadFile(file, CannedAccessControlList.Private);
        book.setFilePath(path);
        update(book);
    }

    public void addImageToBook(Book book,StoredFile file) throws IOException, IllegalArgumentException, FileTooLargeException {
        Long fileSize=getFileSize(file);
        if(fileSize<=4 || fileSize>mainConfig.getAwsS3Config().getMaxImageSize())
            throw new FileTooLargeException("Image size if too large or not defined. Max image size is "+mainConfig.getAwsS3Config().getMaxImageSize());


        if(!awsStorageService.isAllowedImageType(file.getFileName())){
            throw new IllegalArgumentException("Wrong image type");
        }

        String path=awsStorageService.uploadFile(file, CannedAccessControlList.PublicRead);
        String url=awsStorageService.getFileUrl(path);
        book.setPhotoLink(url);
        update(book);
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
        Book book=findById(bookId);
        InputStream inputStream=awsStorageService.getFileInputStream(book.getFilePath());
        String fileName=book.getName()+"."+book.getFilePath().substring(book.getFilePath().lastIndexOf(".")+1);
        return new StoredFile(inputStream,fileName);
    }

    public boolean isTakenByUser(Long userId, Long bookId){
        return bookDao.isTakenByUser(userId,bookId);
    }

    public void takeBook(Long userId, Long bookId, LocalDate returnDate){
        bookDao.takeBook(userId,bookId,LocalDate.now(),returnDate);

        try {
            requestSenderService.postChargeBookFee(userId, bookId);
        }catch(Exception e){
            e.printStackTrace();
        }

        UserBookLog userBookLog =new UserBookLog();
        userBookLog.setUserId(userId);
        userBookLog.setBookId(bookId);
        userBookLog.setAction(Action.TAKE);
        userBookLog.setDate(LocalDateTime.now());

        try {
            requestSenderService.postUserBookLog(userBookLog);
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
           requestSenderService.postUserBookLog(userBookLog);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private Long getFileSize(StoredFile storedFile){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(storedFile.getInputStream(),baos);
        } catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
        storedFile.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return Long.valueOf(baos.size());
    }

    private User getAuthenticatedUser(){
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }
}