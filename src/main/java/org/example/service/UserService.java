package org.example.service;

import org.example.entity.User;

public interface UserService {

    User findUserByUsername(String username);

    void register(String username,String password);
}
