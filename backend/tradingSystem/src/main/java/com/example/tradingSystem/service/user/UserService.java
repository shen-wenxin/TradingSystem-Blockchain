package com.example.tradingSystem.service.user;


import java.util.List;

import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.domain.User.User;



public interface UserService {

    // MYSQL

    int insertUser(User user);
    
    int deleteUser(String id);

    int updataUser(User user);

    User getUser(String id);

    List<User> getAllUsers();









}
