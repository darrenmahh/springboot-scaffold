package org.example.service.impl;

import com.mysql.cj.log.LogFactory;
import org.example.common.Result;
import org.example.dto.request.LoginForm;
import org.example.dto.response.LoginResponse;
import org.example.dto.response.UserInfoResponse;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.exception.CustomerException;
import org.example.mapper.RoleMapper;
import org.example.mapper.UserMapper;
import org.example.mapper.UserRoleMapper;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    // 保证整个方法中的数据库操作要么全部成功 要么全部失败  有一个失败就直接回滚
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

    @Override
    public LoginResponse login(LoginForm loginForm) {
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        // System.out.println(username + password);
        // 判断前端传递过来的数据格式是否正确
        if (username == null || password == null ||
                !username.matches("^\\S{5,16}$") ||
                !password.matches("^\\S{5,16}$")) {
            throw new CustomerException("用户名或密码格式不正确");
        }

        // 根据用户名查询数据库中是否存在此用户
        User user = userMapper.findUserByUsername(username);

        // 查不到用户跑抛出异常不存在
        if (user == null) {
            throw new CustomerException("用户不存在");
        }

        // 判断密码是否正确
        if (Md5Util.checkPassword(password, user.getPassword())) {
            Map<String,Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            String token = JwtUtil.genToken(map, 1000 * 60 * 60 * 24 * 3);
            // token存入redis  时间和token过期时间一致
            stringRedisTemplate.opsForValue().set(token, token, 3, TimeUnit.DAYS);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(token);
            loginResponse.setExpireTime(null);
            loginResponse.setUsername(username);
            return loginResponse;
        } else {
            throw new CustomerException("账号或密码错误");
        }
    }

    @Override
    public UserInfoResponse getUserInfo(Integer id) {
        // 调用mapper层从数据库中读取用户信息
        User user = userMapper.findUserById(id);
        if (user == null) {
            throw new CustomerException("404", "用户不存在");
        }

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(user.getId());
        userInfoResponse.setUsername(user.getUsername());
        userInfoResponse.setCreatedTime(user.getCreatedTime());
        userInfoResponse.setLastLoginTime(user.getLastLoginTime());

        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream().map(Role::getName).toList();
            userInfoResponse.setRoles(roleNames);
        }

        return userInfoResponse;
    }
}
