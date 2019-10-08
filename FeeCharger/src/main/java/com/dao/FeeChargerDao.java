package com.dao;

import com.models.UserBook;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FeeChargerDao {

    @Update("UPDATE users SET money=money-#{fee} WHERE users.id=#{userId}")
    void chargeFee(@Param("userId") Long userId,@Param("fee") BigDecimal fee);

    @Update("UPDATE user_book SET paid_until=#{untilDate} WHERE user_id=#{userId} AND book_id=#{bookId}")
    void extendBookRent(@Param("userId")Long userId, @Param("bookId") Long bookId,@Param("untilDate") LocalDateTime untilDate);

    @Select("SELECT * FROM user_book WHERE paid_until<now() OR paid_until IS NULL")
    @Results(value = {
            @Result(property = "bookId", column = "book_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "takeDate",column = "take_date"),
            @Result(property = "returnDate",column = "return_date"),
            @Result(property = "paidUntil",column = "paid_until")
    })
    List<UserBook> getExpiredBookRent();

    @Select("SELECT * FROM user_book WHERE user_id=#{userId} AND book_id=#{bookId}")
    @Results(value = {
            @Result(property = "bookId", column = "book_id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "takeDate",column = "take_date"),
            @Result(property = "returnDate",column = "return_date"),
            @Result(property = "paidUntil",column = "paid_until")
    })
    UserBook getBookRent(@Param("userId")Long userId, @Param("bookId") Long bookId);
}
