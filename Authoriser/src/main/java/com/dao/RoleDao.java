package com.dao;

import com.models.Role;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleDao {
    @Select(" SELECT roles.* FROM roles " +
            " INNER JOIN user_role ON user_role.role_id=roles.id" +
            " INNER JOIN users ON user_role.user_id=users.id " +
            " WHERE users.id=#{userId} ")
    List<Role> findByUser(@Param("userId") Long userId);


    @Insert("INSERT INTO user_role VALUES(#{userId},#{roleId})")
    void addUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
