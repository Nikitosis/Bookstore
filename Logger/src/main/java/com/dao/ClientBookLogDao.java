package com.dao;

import com.api.ClientBookLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ClientBookLogDao {

    @Select("SELECT * FROM client_book_log WHERE client_id=#{clientId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "clientId",column = "client_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "actionDate",column = "log_date"),
            @Result(property = "action",column = "log_action")
    })
    List<ClientBookLog> findByClientId(@Param("clientId") Long clientId);

    @Select("SELECT * FROM client_book_log WHERE book=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "clientId",column = "client_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "actionDate",column = "log_date"),
            @Result(property = "action",column = "log_action")
    })
    List<ClientBookLog> findByBookId(@Param("bookId") Long bookId);

    @Select("SELECT * FROM client_book_log WHERE client_id=#{clientId} AND book_id=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "clientId",column = "client_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "actionDate",column = "log_date"),
            @Result(property = "action",column = "log_action")
    })
    List<ClientBookLog> findByBookAndClient(@Param("clientId")Long clientId,
                                            @Param("bookId") Long bookId);

    @Select("Select * FROM client_book_log")
    List<ClientBookLog> findAll();

    @Insert("INSERT INTO client_book_log VALUES(NULL,#{clientId},#{bookId},#{actionDate},#{action})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(ClientBookLog clientBookLog);
}
