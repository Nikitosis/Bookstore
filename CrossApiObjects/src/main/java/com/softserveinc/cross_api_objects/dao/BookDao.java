package com.softserveinc.cross_api_objects.dao;

import com.softserveinc.cross_api_objects.models.Book;
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
            @Result(property = "filePath",column = "file_path"),
            @Result(property = "price",column = "price"),
            @Result(property="description",column = "description")
    })
    List<Book> findAll();

    @Select("SELECT books.*,#{userId} as userId FROM books")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isbn",column = "isbn"),
            @Result(property="photoLink", column = "photo_link"),
            @Result(property = "filePath",column = "file_path"),
            @Result(property = "price",column = "price"),
            @Result(property="description",column = "description"),
            @Result(property="isTaken",column = "bookId=id,userId=userId",javaType = Boolean.class,one = @One(select = "isTakenByUser"))
    })
    List<Book> findAllWithUser(@Param("userId")Long curUserId);

    @Select("SELECT * FROM books WHERE id=#{id}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isbn",column = "isbn"),
            @Result(property="photoLink", column = "photo_link"),
            @Result(property = "filePath",column = "file_path"),
            @Result(property = "price",column = "price"),
            @Result(property="description",column = "description")
    })
    Book findById(@Param("id") Long id);

    @Select("SELECT books.*,#{userId} as userId FROM books WHERE id=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isbn",column = "isbn"),
            @Result(property="photoLink", column = "photo_link"),
            @Result(property = "filePath",column = "file_path"),
            @Result(property = "price",column = "price"),
            @Result(property="description",column = "description"),
            @Result(property="isTaken",column = "bookId=id,userId=userId",javaType = Boolean.class,one = @One(select = "isTakenByUser"))
    })
    Book findByIdWithUser(@Param("bookId") Long bookId,@Param("userId") Long curUserId);


    @Insert("INSERT INTO books VALUES(NULL,#{name},#{isbn},#{photoLink},#{filePath},#{price},#{description})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(Book book);

    @Update({
        "<script>",
                "update books",
                "<set>",
                "<if test='name != null'>name = #{name},</if>",
                "<if test='isbn != null'>isbn = #{isbn},</if>",
                "<if test='photoLink != null'>photo_link = #{photoLink},</if>",
                "<if test='filePath != null'>file_path = #{filePath},</if>",
                "<if test='price != null'>price = #{price},</if>",
                "<if test='description != null'>description = #{description},</if>",
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
            @Result(property = "filePath",column = "file_path"),
            @Result(property = "price",column = "price"),
            @Result(property="description",column = "description")
    })
    List<Book> findTakenByUser(@Param("userId") Long userId);

    @Select("SELECT EXISTS " +
            "(SELECT 1 FROM user_book " +
            "WHERE " +
            "user_book.user_id=#{userId} AND " +
            "user_book.book_id=#{bookId} )")
    boolean isTakenByUser(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("INSERT INTO user_book VALUES (#{bookId},#{userId},#{takeDate},#{returnDate},NULL)")
    void takeBook(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("takeDate") LocalDateTime takeDate, @Param("returnDate") LocalDateTime returnDate);

    @Select("DELETE FROM user_book WHERE book_id=#{bookId} AND user_id=#{userId}")
    void returnBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

}
