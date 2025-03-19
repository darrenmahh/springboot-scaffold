package org.example.controller;

import jakarta.validation.constraints.Pattern;
import org.example.annotation.LogOperation;
import org.example.common.Result;
import org.example.dto.request.LoginForm;
import org.example.dto.response.LoginResponse;
import org.example.entity.User;
import org.example.exception.CustomerException;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username,
                           @Pattern(regexp = "^\\S{5,16}$") String password) {
        User user = userService.findUserByUsername(username);

        if (user == null) {
            userService.register(username, password);
            return Result.success();
        } else {
            return Result.error("该用户已存在");
        }
    }

    // @RequestBody意思是把前端发送的数据自动转换成一个java对象并将其赋值
    @PostMapping("/login")
    @LogOperation(value = "用户登录", module = "用户登录")
    public Result login(@RequestBody LoginForm loginForm) {
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        System.out.println(username + password);
        // 判断前端传递过来的数据格式是否正确
        if (username == null || password == null ||
                !username.matches("^\\S{5,16}$") ||
                !password.matches("^\\S{5,16}$")) {
            throw new CustomerException("用户名或密码格式不正确");
        }

        // 根据用户名查询数据库中是否存在此用户
        User user = userService.findUserByUsername(username);

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
            return Result.success(loginResponse);
        } else {
            throw new CustomerException("账号或密码错误");
        }

    }

}
