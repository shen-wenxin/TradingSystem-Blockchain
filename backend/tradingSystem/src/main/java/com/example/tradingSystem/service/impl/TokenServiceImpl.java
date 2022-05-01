package com.example.tradingSystem.service.impl;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.service.TokenService;

import org.springframework.stereotype.Service;

// Token 下发
@Service
public class TokenServiceImpl implements TokenService  {

    @Override
    public String getToken(User user) {
        Date start = new Date();

        long currentTime = System.currentTimeMillis() + 60* 60 * 1000; //一小时有效时间
        Date end = new Date(currentTime);
        String token = JWT.create().withAudience(user.getAccount()).withIssuedAt(start).withExpiresAt(end)
        .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
    
}
