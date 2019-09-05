package com.dao;

import com.models.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BookDao {
    @Select("SELECT * FROM books")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name")
    })
    List<Book> findAll();

    @Select("SELECT * FROM books WHERE id=#{id}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name")
    })
    Book findById(@Param("id") Long id);

    @Insert("INSERT INTO books (name) VALUES(#{name})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(Book client);

    @Update("UPDATE books SET name=#{name} WHERE id=#{id}")
    void update(Book client);

    @Delete("CALL deleteBook(#{id})")
    void delete(@Param("id") Long id);

    @Select("SELECT books.* FROM client_book " +
            "INNER JOIN books ON client_book.book_id=books.id " +
            "WHERE " +
            "client_book.client_id=#{clientId} ")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name")
    })
    List<Book> findTakenByClientId(@Param("clientId") Long clientId);

    @Select("SELECT EXISTS " +
            "(SELECT 1 FROM client_book " +
            "WHERE " +
            "client_book.client_id=#{clientId} AND " +
            "client_book.book_id=#{bookId} )")
    boolean isTakenByClient(@Param("clientId") Long clientId, @Param("bookId") Long bookId);

    @Select("SELECT EXISTS " +
            "(SELECT 1 FROM client_book " +
            "WHERE " +
            "client_book.book_id=#{bookId} )")
    boolean isTaken(@Param("bookId") Long bookId);

    @Select("INSERT INTO client_book VALUES (#{bookId},#{clientId})")
    void takeBook(@Param("clientId") Long clientId,@Param("bookId") Long bookId);

    @Select("DELETE FROM client_book WHERE book_id=#{bookId} AND client_id=#{clientId}")
    void returnBook(@Param("clientId") Long clientId,@Param("bookId") Long bookId);

}
