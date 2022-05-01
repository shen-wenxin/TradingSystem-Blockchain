package com.example.tradingSystem.entry.blockchain;

import java.util.List;

import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;

public interface BlockChainMapper {

    // String getSuperviserList();


    // Superviser
    Superviser getSuperviser(String id);

    Customer getCustomer(String id);


}
