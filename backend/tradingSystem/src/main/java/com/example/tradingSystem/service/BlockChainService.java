package com.example.tradingSystem.service;

import com.alibaba.fastjson.JSONObject;
import com.example.tradingSystem.domain.User.Business;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.domain.User.User;

public interface BlockChainService {

    //Superviser
    Superviser getSuperviserOnChain(String id);

    //Customer
    Customer getCustomerOnChain(String id);

    // Business
    Business getBussinessOnChain(String id);

    // userController 
    Boolean userExist(String id, Integer role);
    Boolean userRegister(User user);
    JSONObject getUserInfoOnChain(String id, Integer role);

    




    
}
