package org.example.service.impl;

import com.mysql.cj.log.LogFactory;
import org.example.entity.User;
import org.example.exception.CustomerException;
import org.example.mapper.RoleMapper;
import org.example.mapper.UserMapper;
import org.example.mapper.UserRoleMapper;
import org.example.service.UserService;
import org.example.utils.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    @Transactional
    public void register(String username, String password) {
        try {
            // 检查用户名是否已存在
            User existingUser = userMapper.findUserByUsername(username);
            if (existingUser != null) {
                throw new CustomerException("400", "用户已存在");
            }

            String md5String = Md5Util.getMD5String(password);

            User user = new User();
            // 选取注册用户的userID
            user.setUsername(username);
            user.setPassword(md5String);
            user.setCreatedTime(LocalDateTime.now());
            userMapper.add(user);

            int userId = user.getId();

            int roleId = roleMapper.findRoleByName("ROLE_USER");

            if (roleId <= 0) {
                throw new CustomerException("500","用户不存在");
            }

            userRoleMapper.addUserRole(userId, roleId);

            logger.info("用户注册成功: {} (ID:{})",username, userId);
        } catch (CustomerException ce) {
            throw ce;
        } catch (Exception e) {
            logger.error("用户注册失败:{}", username, e);
            throw new CustomerException("500", "注册失败" + e.getMessage());
        }

    }
}
