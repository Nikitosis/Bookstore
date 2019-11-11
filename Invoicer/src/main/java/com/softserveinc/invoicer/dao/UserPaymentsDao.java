package com.softserveinc.invoicer.dao;

import com.softserveinc.cross_api_objects.api.UserBookPaymentLog;
import com.softserveinc.invoicer.models.UserPayments;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

public interface UserPaymentsDao {

    @Select("SELECT user_id,#{fromDate} as from_date FROM user_book_payment_log as log WHERE log_date>=#{fromDate} GROUP BY user_id")
    @Results(value = {
            @Result(property = "userId",column = "user_id"),
            @Result(property = "paymentLogs", javaType = List.class,column = "{userId=user_id,fromDate=from_date}",
                many = @Many(select="findUserBookPaymentsByUserIdAfterDate"))
    })
    List<UserPayments> findUserPaymentsFromDate(@Param("fromDate") LocalDateTime localDateTime);

    @Select("SELECT * FROM user_book_payment_log WHERE user_id=#{userId} AND log_date>=#{fromDate}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "userId",column = "user_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "date",column = "log_date"),
            @Result(property = "payment",column = "payment")
    })
    List<UserBookPaymentLog> findUserBookPaymentsByUserIdAfterDate(@Param("userId") Long userId,@Param("fromDate") LocalDateTime localDateTime);
}
