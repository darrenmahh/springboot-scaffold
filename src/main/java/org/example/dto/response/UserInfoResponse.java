package org.example.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserInfoResponse {
    private Integer id;
    private String username;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdTime;
    private List<String> roles; // 角色列表
}
