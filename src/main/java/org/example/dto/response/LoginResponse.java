package org.example.dto.response;

import lombok.Data;

import java.time.LocalDate;

// 登陆成功之后系统给的凭证
@Data
public class LoginResponse {
    private String username;
    // 登录成功之后给的令牌
    private String accessToken;
    // 令牌过期时间
    private LocalDate expireTime; // xxxx/xx/xx xx:xx:xx
}
