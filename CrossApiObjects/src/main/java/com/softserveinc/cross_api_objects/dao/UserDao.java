package com.softserveinc.cross_api_objects.dao;

import com.softserveinc.cross_api_objects.models.Role;
import com.softserveinc.cross_api_objects.models.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDao {
    @Select("SELECT * FROM users")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name"),
            @Result(property = "country",column = "country"),
            @Result(property = "city",column = "city"),
            @Result(property = "gender",column = "gender"),
            @Result(property = "email",column = "email"),
            @Result(property = "phone",column = "phone"),
            @Result(property = "avatarLink",column = "avatar_link"),
            @Result(property = "money",column = "money"),
            @Result(property = "isEmailVerified",column = "is_email_verified"),
            @Result(property = "verificationToken",column = "verification_token"),
            @Result(property="roles",javaType = List.class,column = "id",many = @Many(select = "findRolesByUser"))
    })
    List<User> findAll();

    @Select("SELECT * FROM users WHERE username=#{username}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name"),
            @Result(property = "country",column = "country"),
            @Result(property = "city",column = "city"),
            @Result(property = "gender",column = "gender"),
            @Result(property = "email",column = "email"),
            @Result(property = "phone",column = "phone"),
            @Result(property = "avatarLink",column = "avatar_link"),
            @Result(property = "money",column = "money"),
            @Result(property = "isEmailVerified",column = "is_email_verified"),
            @Result(property = "verificationToken",column = "verification_token"),
            @Result(property="roles",javaType = List.class,column = "id",many = @Many(select = "findRolesByUser"))
    })
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM users WHERE id=#{userId}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name"),
            @Result(property = "country",column = "country"),
            @Result(property = "city",column = "city"),
            @Result(property = "gender",column = "gender"),
            @Result(property = "email",column = "email"),
            @Result(property = "phone",column = "phone"),
            @Result(property = "avatarLink",column = "avatar_link"),
            @Result(property = "money",column = "money"),
            @Result(property = "isEmailVerified",column = "is_email_verified"),
            @Result(property = "verificationToken",column = "verification_token"),
            @Result(property="roles",javaType = List.class,column = "id",many = @Many(select = "findRolesByUser"))
    })
    User findById(@Param("userId") Long userId);

    @Select(" SELECT roles.* FROM roles " +
            " INNER JOIN user_role ON user_role.role_id=roles.id" +
            " INNER JOIN users ON user_role.user_id=users.id " +
            " WHERE users.id=#{userId} ")
    List<Role> findRolesByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM users WHERE verification_token=#{verificationToken}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username",column = "username"),
            @Result(property = "password",column = "password"),
            @Result(property = "fName",column = "first_name"),
            @Result(property = "lName",column = "last_name"),
            @Result(property = "country",column = "country"),
            @Result(property = "city",column = "city"),
            @Result(property = "gender",column = "gender"),
            @Result(property = "email",column = "email"),
            @Result(property = "phone",column = "phone"),
            @Result(property = "avatarLink",column = "avatar_link"),
            @Result(property = "money",column = "money"),
            @Result(property = "isEmailVerified",column = "is_email_verified"),
            @Result(property = "verificationToken",column = "verification_token"),
            @Result(property="roles",javaType = List.class,column = "id",many = @Many(select = "findRolesByUser"))
    })
    User findByVerificationToken(@Param("verificationToken") String verificationToken);

    @Insert("INSERT INTO users VALUES(NULL,#{username},#{password},#{fName},#{lName},#{country},#{city},#{gender},#{email},#{phone},#{avatarLink},DEFAULT,#{isEmailVerified},#{verificationToken})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    Long save(User user);

    @Update({
            "<script>",
            "update users",
            "<set>",
            "<if test='password != null'>password = #{password},</if>",
            "<if test='fName != null'>first_name = #{fName},</if>",
            "<if test='lName != null'>last_name = #{lName},</if>",
            "<if test='country != null'>country = #{country},</if>",
            "<if test='city != null'>city = #{city},</if>",
            "<if test='gender != null'>gender = #{gender},</if>",
            "<if test='email != null'>email = #{email},</if>",
            "<if test='phone != null'>phone = #{phone},</if>",
            "<if test='avatarLink != null'>avatar_link = #{avatarLink},</if>",
            "<if test='money != null'>money = #{money},</if>",
            "<if test='isEmailVerified != null'>is_email_verified = #{isEmailVerified},</if>",
            "<if test='verificationToken != null'>verification_token = #{verificationToken},</if>",
            "</set>",
            "WHERE id=#{id}",
            "</script>"
    })
//    @Update("UPDATE users SET <if test='username!=null'>=#{username}, password=#{password}, first_name=#{fName}, last_name=#{lName} WHERE id=#{id}")
    void update(User user);

    @Delete("CALL deleteUser(#{userId})")
    void delete(@Param("userId") Long userId);
}
