package com.softserveinc.logger.dao;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserBookPaymentLogDao {
    @Select("SELECT * FROM user_book_payment_log WHERE user_id=#{userId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "payment",column = "payment")
    })
    List<UserBookPaymentLog> findByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM user_book_payment_log WHERE book=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "payment",column = "payment")
    })
    List<UserBookPaymentLog> findByBookId(@Param("bookId") Long bookId);

    @Select("SELECT * FROM user_book_payment_log WHERE user_id=#{userId} AND book_id=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "payment",column = "payment")
    })
    List<UserBookPaymentLog> findByBookAndUser(@Param("userId") Long userId,
                                        @Param("bookId") Long bookId);

    @Select("Select * FROM user_book_payment_log")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "payment",column = "payment")
    })
    List<UserBookPaymentLog> findAll();

    @Insert("INSERT INTO user_book_payment_log VALUES(NULL,#{userId},#{bookId},#{date},#{payment})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(UserBookPaymentLog userBookLog);
}
