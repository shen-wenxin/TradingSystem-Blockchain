package com.example.tradingSystem.service.impl;

import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.entry.blockchain.BlockChainMapper;
import com.example.tradingSystem.service.user.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;

public class CustomerServiceImpl implements CustomerService{

    @Autowired 
    private BlockChainMapper mapper;

    @Override
    public Customer getCustomerOnChain(String id) {
        
        return null;
    }
    
}
