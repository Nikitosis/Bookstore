package com.dao;

import com.models.ClientBookLog;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientBookLogDao {

    @Select("SELECT * FROM client_book_log WHERE client_id=#{clientId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "clientId",column = "client_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "startDate",column = "start_date"),
            @Result(property = "endDate",column = "end_date")
    })
    List<ClientBookLog> findByClientId(Long clientId);

    @Select("SELECT * FROM client_book_log WHERE book=#{bookId}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "clientId",column = "client_id"),
            @Result(property = "bookId",column = "book_id"),
            @Result(property = "startDate",column = "start_date"),
            @Result(property = "endDate",column = "end_date")
    })
    List<ClientBookLog> findByBookId(Long bookId);
}
