package com.softserveinc.logger.dao;

import com.softserveinc.cross_api_objects.api.UserBookLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserBookLogDao {

    @Select("SELECT * FROM user_book_log WHERE user_id=#{userId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "action",column = "log_action")
    })
    List<UserBookLog> findByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM user_book_log WHERE book=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "action",column = "log_action")
    })
    List<UserBookLog> findByBookId(@Param("bookId") Long bookId);

    @Select("SELECT * FROM user_book_log WHERE user_id=#{userId} AND book_id=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "action",column = "log_action")
    })
    List<UserBookLog> findByBookAndUser(@Param("userId") Long userId,
                                        @Param("bookId") Long bookId);

    @Select("Select * FROM user_book_log")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "action",column = "log_action")
    })
    List<UserBookLog> findAll();

    @Insert("INSERT INTO user_book_log VALUES(NULL,#{userId},#{bookId},#{date},#{action})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(UserBookLog userBookLog);

    @Select("SELECT count(*) FROM user_book_log WHERE book_id=#{bookId}")
    Long getBookTakenAmount(@Param("bookId") Long bookId);

    @Select("SELECT count(*) FROM user_book_log WHERE book_id=#{bookId}")
    Long getBookReturnedAmount(@Param("bookId") Long bookId);
}
