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
    Long save(Book book);

    @Update("UPDATE books SET name=#{name} WHERE id=#{id}")
    void update(Book book);

    @Delete("CALL deleteBook(#{id})")
    void delete(@Param("id") Long id);

    @Select("SELECT books.* FROM user_book " +
            "INNER JOIN books ON user_book.book_id=books.id " +
            "WHERE " +
            "user_book.user_id=#{userId} ")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name")
    })
    List<Book> findTakenByUser(@Param("userId") Long userId);

    @Select("SELECT EXISTS " +
            "(SELECT 1 FROM user_book " +
            "WHERE " +
            "user_book.user_id=#{userId} AND " +
            "user_book.book_id=#{bookId} )")
    boolean isTakenByUser(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("SELECT EXISTS " +
            "(SELECT 1 FROM user_book " +
            "WHERE " +
            "user_book.book_id=#{bookId} )")
    boolean isTaken(@Param("bookId") Long bookId);

    @Select("INSERT INTO user_book VALUES (#{bookId},#{userId})")
    void takeBook(@Param("userId") Long userId,@Param("bookId") Long bookId);

    @Select("DELETE FROM user_book WHERE book_id=#{bookId} AND user_id=#{userId}")
    void returnBook(@Param("userId") Long userId,@Param("bookId") Long bookId);

}
