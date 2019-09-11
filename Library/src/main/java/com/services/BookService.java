package com.services;

import com.MainConfig;
import com.api.Action;
import com.api.ClientBookLog;
import com.dao.BookDao;
import com.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.sql.Date;
import java.util.List;
import java.util.Calendar;

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

    public List<Book> findTakenByClientId(Long clientId){
        return bookDao.findTakenByClientId(clientId);
    }

    public boolean isTakenByClient(Long clientId,Long bookId){
        return bookDao.isTakenByClient(clientId,bookId);
    }

    public boolean isTaken(Long bookId){
        return bookDao.isTaken(bookId);
    }

    public void takeBook(Long clientId,Long bookId){
        bookDao.takeBook(clientId,bookId);

        ClientBookLog clientBookLog=new ClientBookLog();
        clientBookLog.setClientId(clientId);
        clientBookLog.setBookId(bookId);
        clientBookLog.setAction(Action.TAKE);
        clientBookLog.setActionDate(new Date(Calendar.getInstance().getTime().getTime()));

        try {
            postClientBookLog(clientBookLog);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void returnBook(Long clientId,Long bookId){
        bookDao.returnBook(clientId,bookId);

        ClientBookLog clientBookLog=new ClientBookLog();
        clientBookLog.setClientId(clientId);
        clientBookLog.setBookId(bookId);
        clientBookLog.setAction(Action.RETURN);
        clientBookLog.setActionDate(new Date(Calendar.getInstance().getTime().getTime()));

        try {
           postClientBookLog(clientBookLog);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void postClientBookLog(ClientBookLog clientBookLog){
        Client client = ClientBuilder.newClient();
        client.target(mainConfig.getClientBookLoggerService().getUrl())
                    .path("/actions")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(clientBookLog, MediaType.APPLICATION_JSON));
    }
}
