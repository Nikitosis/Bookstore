package com.crossapi.dao;

import com.crossapi.models.Book;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
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

    @Insert("INSERT INTO books VALUES(NULL,#{name},#{isbn},#{photoLink},#{url},#{dailyPrice})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(Book book);

    @Update({
        "<script>",
                "update books",
                "<set>",
                "<if test='name != null'>name = #{name},</if>",
                "<if test='isbn != null'>isbn = #{isbn},</if>",
                "<if test='photoLink != null'>photo_link = #{photoLink},</if>",
                "<if test='url != null'>url = #{url},</if>",
                "<if test='dailyPrice != null'>daily_price = #{dailyPrice},</if>",
                "</set>",
                "WHERE id=#{id}",
                "</script>"
    })
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

    @Select("INSERT INTO user_book VALUES (#{bookId},#{userId},#{takeDate},#{returnDate},NULL)")
    void takeBook(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("takeDate") LocalDate takeDate, @Param("returnDate") LocalDate returnDate);

    @Select("DELETE FROM user_book WHERE book_id=#{bookId} AND user_id=#{userId}")
    void returnBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

}
