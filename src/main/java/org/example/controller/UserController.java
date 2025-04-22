package org.example.controller;

import jakarta.validation.constraints.Pattern;
import org.example.annotation.LogOperation;
import org.example.common.Result;
import org.example.dto.request.LoginForm;
import org.example.dto.response.LoginResponse;
import org.example.dto.response.UserInfoResponse;
import org.example.entity.User;
import org.example.exception.CustomerException;
import org.example.service.UserService;
import org.example.utils.JwtUtil;
import org.example.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/register")
    @LogOperation(value = "用户注册", module = "用户管理")
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
        try {
            LoginResponse response = userService.login(loginForm);
            return Result.success(response);
        } catch (CustomerException e) {
            return Result.error(e.getMessage());
        }

    }

    @GetMapping("/info")
    @LogOperation(value = "获取用户信息", module = "用户管理")
    public Result<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token == null) {
            return Result.error("401","未授权，请先登录");
        }


        if (!JwtUtil.validateToken(token)) {
            return Result.error("401","Token已过期或无效");
        }
        try {
            Map<String,Object> claims = JwtUtil.parseToken(token);
            UserInfoResponse userInfoResponse =  userService.getUserInfo((Integer) claims.get("id"));
            // System.out.println("--------------" + userInfoResponse);
            return Result.success(userInfoResponse);
        } catch (Exception e) {
           return Result.error("获取用户信息失败" + e.getMessage());
        }
    }

}
