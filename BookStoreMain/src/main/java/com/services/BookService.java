package com.services;

import com.dao.BookDao;
import com.models.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private BookDao bookDao;

    @Autowired
    public BookService(BookDao bookDao){
        this.bookDao=bookDao;
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
    }

    public void returnBook(Long clientId,Long bookId){
        bookDao.returnBook(clientId,bookId);
    }
}
