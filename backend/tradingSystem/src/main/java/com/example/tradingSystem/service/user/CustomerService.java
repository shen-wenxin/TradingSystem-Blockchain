package com.example.tradingSystem.service.user;

import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;

public interface CustomerService {
    Customer getCustomerOnChain(String id);


}
