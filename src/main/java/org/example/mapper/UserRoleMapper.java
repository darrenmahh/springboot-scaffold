package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper {
    void addUserRole(@Param("userId") int userId,@Param("roleId") int roleId);
}
