package com.example.taskmanagement.service;

import com.example.taskmanagement.model.User;

import java.util.List;

public interface IUserService {
    void register(String email, String password, String confirmPassword);
    User login(String email, String password);
    List<User> getAllUsers();
}
