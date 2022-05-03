package com.example.tradingSystem.entry.blockchain;

import java.util.List;

import com.example.tradingSystem.domain.User.Business;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;

public interface BlockChainMapper {

    // Fabric Blockchain data
    Boolean DataOnChainCheck(String id);


    // Superviser
    Superviser getSuperviser(String id);
    Boolean createSuperviser(String id, String name, String phone);



    // Customer
    Customer getCustomer(String id);
    Boolean createCustomer(String id, String name, String phone);



    // Business 
    Business getBusiness(String id);
    Boolean createBusiness(String id, String name, String phone);



}
