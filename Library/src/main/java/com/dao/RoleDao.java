package com.dao;

import com.models.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleDao {
    @Select(" SELECT roles.* FROM roles " +
            " INNER JOIN user_role ON user_role.role_id=roles.name" +
            " INNER JOIN users ON user_role.user_id=users.username " +
            " WHERE users.username=#{username} ")
    List<Role> findByUsername(@Param("username") String username);
}
