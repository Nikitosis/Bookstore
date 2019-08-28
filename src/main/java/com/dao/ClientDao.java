package com.dao;

import com.models.Client;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ClientDao {
    @Select("SELECT * FROM clients")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "fname",column = "first_name"),
            @Result(property = "lname",column = "last_name")
    })
    List<Client> getAll();
}
