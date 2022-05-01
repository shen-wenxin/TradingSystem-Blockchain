package com.example.tradingSystem.service;

import com.example.tradingSystem.domain.User.User;

public interface TokenService {

    public String getToken(User user);
    
}
