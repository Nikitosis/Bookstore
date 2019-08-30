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
    Book findById(@Param("id") Long id);

    @Insert("INSERT INTO books (name,is_taken) VALUES(#{name},#{isTaken})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(Book client);

    @Update("UPDATE books SET name=#{name},is-taken=#{isTaken} WHERE id=#{id}")
    void update(Book client);

    @Delete("DELETE FROM books WHERE id=#{id}")
    void delete(@Param("id") Long id);

    @Select("SELECT books.* FROM client_book_log " +
            "INNER JOIN books ON client_book_log.book_id=books.id " +
            "WHERE " +
            "books.is_taken=TRUE AND " +
            "client_book_log.client_id=#{clientId} AND " +
            "client_book_log.end_date IS NULL ")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isTaken",column = "is_taken")
    })
    List<Book> findTakenByClientId(@Param("clientId") Long clientId);

    @Select("SELECT EXISTS " +
            "(SELECT 1 FROM client_book_log " +
            "INNER JOIN books ON client_book_log.book_id=books.id " +
            "WHERE " +
            "books.is_taken=TRUE AND " +
            "client_book_log.client_id=#{clientId} AND " +
            "client_book_log.book_id=#{bookId} AND " +
            "client_book_log.end_date IS NULL) ")
    boolean isTaken(@Param("clientId") Long clientId,@Param("bookId") Long bookId);

    @Select("CALL takeBook(#{clientId},#{bookId})")
    void takeBook(@Param("clientId") Long clientId,@Param("bookId") Long bookId);

    @Select("CALL returnBook(#{clientId},#{bookId})")
    void returnBook(@Param("clientId") Long clientId,@Param("bookId") Long bookId);

}
