package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.User;

@Mapper
public interface UserMapper {

    User findUserByUsername(@Param("username") String username);

    void add(@Param("username") String username, @Param("password") String password);
}
