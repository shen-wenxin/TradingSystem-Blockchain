package com.example.tradingSystem.service.impl;

import java.util.List;

import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.entry.user.UserMapper;
import com.example.tradingSystem.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired 
    private UserMapper mapper;

    @Override
    public int insertUser(User user) {
        return mapper.insert(user.getAccount(), user.getName(), user.getPassword(), user.getRole());
    }

    @Override
    public int deleteUser(String account) {
        return mapper.delete(account);
    }

    @Override
    public int updataUser(User user) {
        return mapper.update(user.getAccount(), user.getPassword());
    }

    @Override
    public User getUser(String account) {
        return mapper.getUserByName(account);
    }

    @Override
    public List<User> getAllUsers() {
        return mapper.getAllUser();
    }
   
}
