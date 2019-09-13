package com.dao;

import com.models.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDao {
    @Select("SELECT * FROM users")
    @Results(value = {
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name")
    })
    List<User> findAll();

    @Select("SELECT * FROM users WHERE username=#{username}")
    @Results(value = {
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name")
    })
    User findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users (first_name,last_name) VALUES(#{fName},#{lName})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(User user);

    @Insert("INSERT INTO user_role VALUES(#{username},#{roleName})")
    void addRole(@Param("username") String username, @Param("roleName") String roleName);

    @Update("UPDATE users SET first_name=#{fName}, last_name=#{lName} WHERE username=#{username}")
    void update(User user);

    @Delete("CALL deleteUser(#{username})")
    void delete(@Param("username") String username);
}
