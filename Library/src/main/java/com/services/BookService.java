package com.services;

import com.MainConfig;
import com.api.Action;
import com.api.UserBookLog;
import com.dao.BookDao;
import com.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookService {

    private BookDao bookDao;
    private MainConfig mainConfig;

    @Autowired
    public BookService(BookDao bookDao, MainConfig mainConfig) {
        this.bookDao = bookDao;
        this.mainConfig = mainConfig;
    }

    public List<Book> findAll(){
        return bookDao.findAll();
    }

    public Book findById(Long id){
        return bookDao.findById(id);
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

    public List<Book> findTakenByUsername(String username){
        return bookDao.findTakenByUsername(username);
    }

    public boolean isTakenByUser(String username, Long bookId){
        return bookDao.isTakenByUser(username,bookId);
    }

    public boolean isTaken(Long bookId){
        return bookDao.isTaken(bookId);
    }

    public void takeBook(String username,Long bookId){
        bookDao.takeBook(username,bookId);

        UserBookLog userBookLog =new UserBookLog();
        userBookLog.setUserId(username);
        userBookLog.setBookId(bookId);
        userBookLog.setAction(Action.TAKE);
        userBookLog.setActionDate(LocalDateTime.now());

        try {
            postUserBookLog(userBookLog);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void returnBook(String username,Long bookId){
        bookDao.returnBook(username,bookId);

        UserBookLog userBookLog =new UserBookLog();
        userBookLog.setUserId(username);
        userBookLog.setBookId(bookId);
        userBookLog.setAction(Action.RETURN);
        userBookLog.setActionDate(LocalDateTime.now());

        System.out.println(Entity.entity(userBookLog,MediaType.APPLICATION_JSON).toString());

        try {
           postUserBookLog(userBookLog);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void postUserBookLog(UserBookLog userBookLog){
        Client client = ClientBuilder.newClient();
        client.target(mainConfig.getClientBookLoggerService().getUrl())
                    .path("/actions")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(userBookLog, MediaType.APPLICATION_JSON));
    }
}
