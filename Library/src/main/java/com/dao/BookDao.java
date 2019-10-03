package com.dao;

import com.models.Book;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BookDao {
    @Select("SELECT * FROM books")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isbn",column = "isbn"),
            @Result(property="photoLink", column = "photo_link"),
            @Result(property = "url",column = "url"),
            @Result(property = "dailyPrice",column = "daily_price"),
    })
    List<Book> findAll();

    @Select("SELECT * FROM books WHERE id=#{id}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isbn",column = "isbn"),
            @Result(property="photoLink", column = "photo_link"),
            @Result(property = "url",column = "url"),
            @Result(property = "dailyPrice",column = "daily_price"),
    })
    Book findById(@Param("id") Long id);

    @Insert("INSERT INTO books VALUES(NULL,#{name},#{isbn},#{photoLink})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(Book book);

    @Update("UPDATE books SET name=#{name}, isbn=#{isbn}, photo_link=#{photoLink} WHERE id=#{id}")
    void update(Book book);

    @Delete("CALL deleteBook(#{id})")
    void delete(@Param("id") Long id);

    @Select("SELECT books.* FROM user_book " +
            "INNER JOIN books ON user_book.book_id=books.id " +
            "WHERE " +
            "user_book.user_id=#{userId} ")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isbn",column = "isbn"),
            @Result(property="photoLink", column = "photo_link"),
            @Result(property = "url",column = "url"),
            @Result(property = "dailyPrice",column = "daily_price"),
    })
    List<Book> findTakenByUser(@Param("userId") Long userId);

    @Select("SELECT EXISTS " +
            "(SELECT 1 FROM user_book " +
            "WHERE " +
            "user_book.user_id=#{userId} AND " +
            "user_book.book_id=#{bookId} )")
    boolean isTakenByUser(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("INSERT INTO user_book VALUES (#{bookId},#{userId},#{takeDate},#{returnDate})")
    void takeBook(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("takeDate") LocalDate takeDate, @Param("returnDate")LocalDate returnDate);

    @Select("DELETE FROM user_book WHERE book_id=#{bookId} AND user_id=#{userId}")
    void returnBook(@Param("userId") Long userId,@Param("bookId") Long bookId);

}
