package com.example.tradingSystem.entry.blockchain;

import java.util.List;

import com.example.tradingSystem.domain.Commodity.Commodity;
import com.example.tradingSystem.domain.Trade.Account;
import com.example.tradingSystem.domain.Trade.Trade;
import com.example.tradingSystem.domain.User.Business;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;

public interface BlockChainMapper {

    // Fabric Blockchain data
    Boolean DataOnChainCheck(String id);

    Boolean ExistAccountCheck(String userId, String month, String year);


    // Superviser
    Superviser getSuperviser(String id);

    Boolean createSuperviser(String id, String name, String phone);



    // Customer
    Customer getCustomer(String id);

    Boolean createCustomer(String id, String name, String phone);

    Customer getCustomerById(String id);

    Integer getCusSpentByMonth(String cId);

    List<Customer> getAllCustomers();




    // Business 
    Business getBusiness(String id);

    Business getBusinessById(String businessId);

    Boolean createBusiness(String id, String name, String phone);

    Integer getBusProfitByMonth(String bId);

    List<Business> getAllBusiness();




    // commodity
    Boolean createCommodity(String name, String price, String issuer, String isserName);

    List<Commodity> getCommodityOnSaleByIssuer(String issuer);

    List<Commodity> getCommodityOnSale();

    List<Commodity> getCommodityBaughtByCus(String customer);

    List<Commodity> getCommoditySaledByIssuer(String issuer);

    List<Commodity> getAllComodity();


    // trade
    Boolean createBuyTrade(String tTime, String price, String bId, String sId, String cId);

    Account getAccountByUserMonth(String userId, String month, String year);

    List<Trade> getAllTrade();


    // account
    void monthlyAccountCheck();
    












}
