package com.dao;

import com.models.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BookDao {
    @Select("SELECT * FROM books")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isTaken",column = "is_taken")
    })
    List<Book> findAll();

    @Select("SELECT * FROM books WHERE id=#{id}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isTaken",column = "is_taken")
    })
    Book findById(Long id);

    @Insert("INSERT INTO books (name) VALUES(#{name})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(Book client);

    @Update("UPDATE books SET name=#{name} WHERE id=#{id}")
    void update(Book client);

    @Delete("DELETE FROM books WHERE id=#{id}")
    void delete(Long id);

    @Select("SELECT books.* FROM client_book_log " +
            "INNER JOIN books ON client_book_log.book_id=books.id" +
            "WHERE books.is_taken=TRUE AND client_book_log.client_id=#{clientId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isTaken",column = "is_taken")
    })
    List<Book> findTakenByClientId(Long clientId);

    //TODO: use stored procedure for this methods
    void takeBook(Long clientId,Long bookId);

    void returnBook(Long clientId,Long bookId);

}
