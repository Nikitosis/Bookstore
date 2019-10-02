package com.crossapi.dao;

import com.crossapi.models.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDao {
    @Select("SELECT * FROM users")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name")
    })
    List<User> findAll();

    @Select("SELECT * FROM users WHERE username=#{username}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name")
    })
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM users WHERE id=#{userId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name")
    })
    User findById(@Param("userId") Long userId);

    @Insert("INSERT INTO users (username,password,first_name,last_name) VALUES(#{username},#{password},#{fName},#{lName})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(User user);

    @Update({
            "<script>",
            "update users",
            "<set>",
            "<if test='username != null'>username = #{username},</if>",
            "<if test='password != null'>password = #{password},</if>",
            "<if test='fName != null'>first_name = #{fName},</if>",
            "<if test='lName != null'>last_name = #{lName},</if>",
            "</set>",
            "WHERE id=#{id}",
            "</script>"
    })
//    @Update("UPDATE users SET <if test='username!=null'>=#{username}, password=#{password}, first_name=#{fName}, last_name=#{lName} WHERE id=#{id}")
    void update(User user);

    @Delete("CALL deleteUser(#{userId})")
    void delete(@Param("userId") Long userId);
}
