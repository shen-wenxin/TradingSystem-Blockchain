package com.example.tradingSystem.service;

import java.util.List;

import javax.activation.CommandObject;

import com.alibaba.fastjson.JSONObject;
import com.example.tradingSystem.domain.Commodity.Commodity;
import com.example.tradingSystem.domain.Trade.Account;
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
    Business getBussinessOnChainById(String busId);

    // userController 
    Boolean userExist(String id, Integer role);

    Boolean userExist(String id);

    Boolean userRegister(User user);

    JSONObject getUserInfoOnChain(String id, Integer role);

    String getUserNameById(String id);

    // commodity
    Boolean commodityCreate(String name, String price, String issuer, String issuerName);

    List<Commodity> getCommodityOnSaleByIssuer(String issuer);

    List<Commodity> getCommodityOnSale();

    List<Commodity> getCommodityBaughtByCus(String customer);

    List<Commodity> getCommoditySaledByBus(String bus);

    // trade 

    Boolean createBuyTrade(String tTime, String price, String bId, String sId, String cId);
    Integer getBusProfitByMonth(String bId);

    List<Account> getBusAccountList(String bId);




    




    
}
