package com.example.tradingSystem.domain.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private String account;

    private String password;

    private String name;

    //0: 监管员 1：商家 2：消费者
    private int role;

    public User(String account, String password, int role){
        this.account = account;
        this.password = password;
        this.role = role;
    } 
}
