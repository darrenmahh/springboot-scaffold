package org.example.service;

import org.example.dto.request.LoginForm;
import org.example.dto.response.LoginResponse;
import org.example.dto.response.UserInfoResponse;
import org.example.entity.User;

public interface UserService {

    User findUserByUsername(String username);

    void register(String username, String password);

    LoginResponse login(LoginForm loginForm);

    UserInfoResponse getUserInfo(Integer id);
}
