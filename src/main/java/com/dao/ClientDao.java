package com.dao;

import com.models.Client;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ClientDao {
    @Select("SELECT * FROM clients")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name")
    })
    List<Client> findAll();

    @Select("SELECT * FROM clients WHERE id=#{id}")
    @Results(value = {
            @Result(property = "id",column = "id"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name")
    })
    Client findById(@Param("id") Long id);

    @Insert("INSERT INTO clients (first_name,last_name) VALUES(#{fName},#{lName})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(Client client);

    @Update("UPDATE clients SET first_name=#{fName}, last_name=#{lName} WHERE id=#{id}")
    void update(Client client);

    @Delete("CALL deleteClient(#{id})")
    void delete(@Param("id") Long id);
}
