package com.example.tradingSystem.service.impl;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.Commodity.Commodity;
import com.example.tradingSystem.domain.User.Business;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.entry.blockchain.BlockChainMapper;
import com.example.tradingSystem.service.BlockChainService;
import com.example.tradingSystem.web.exception.BussinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class BlockChainServiceImpl implements BlockChainService{

    @Autowired 
    private BlockChainMapper mapper;

    @Override
    public Superviser getSuperviserOnChain(String id) {
        return mapper.getSuperviser(id);
    }

    @Override
    public Business getBussinessOnChain(String id) {
        return mapper.getBusiness(id);
    }
    
    @Override
    public Customer getCustomerOnChain(String id) {
        return mapper.getCustomer(id);
    }

    @Override
    public Boolean userExist(String id, Integer role) {
        if (role == Constant.ROLE_TYPE_SUPERVISER){
            id = Constant.PREFIX_ID_SUPERVISER + id;
            return mapper.DataOnChainCheck(id);
        
        }else if(role == Constant.ROLE_TYPE_CUSTOMER){
            id = Constant.PREFIX_ID_CUSTOMER + id;
            return mapper.DataOnChainCheck(id);

        }else{
            id = Constant.PREFIX_ID_BUSSINIESSMAN + id;
            return mapper.DataOnChainCheck(id);
        }
    }

    @Override
    public Boolean userExist(String id) {
        return mapper.DataOnChainCheck(id);
    }

    @Override
    public Boolean userRegister(User user) {
        Integer roleType = user.getRole();
        if (roleType == Constant.ROLE_TYPE_SUPERVISER){
            return superviserRegister(user);
        }
        if (roleType == Constant.ROLE_TYPE_BUSSINESS){
            return businessRegister(user);
        }
        if (roleType == Constant.ROLE_TYPE_CUSTOMER){
            return customerRegister(user);
        }
        log.error("role type not exist");
        return false;
    }

    private Boolean superviserRegister(User user){
        String account = user.getAccount();
        String name = user.getName();
        String phone = user.getAccount();
        return mapper.createSuperviser(account, name, phone);
    }

    private Boolean businessRegister(User user){
        String account = user.getAccount();
        String name = user.getName();
        String phone = user.getAccount();
        return mapper.createBusiness(account, name, phone);
    }
    
    private Boolean customerRegister(User user){
        String account = user.getAccount();
        String name = user.getName();
        String phone = user.getAccount();
        return mapper.createCustomer(account, name, phone);
    }

    @Override
    public String getUserNameById(String id) {
        String name = "";
        if (id.startsWith(Constant.PREFIX_ID_SUPERVISER)){
            Superviser sup = getSuperviserOnChain(id.substring(4));
            name = sup.getName();
        }else if(id.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN)){
            Business bus = getBussinessOnChain(id.substring(4));
            name = bus.getName();
        }else if(id.startsWith(Constant.PREFIX_ID_CUSTOMER)){
            Customer cus = getCustomerOnChain(id.substring(4));
            name = cus.getName();
        }else{
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        return name;
    }


    @Override
    public JSONObject getUserInfoOnChain(String id, Integer role) {
        JSONObject jsonObj = new JSONObject();
        if (role ==  Constant.ROLE_TYPE_SUPERVISER){
            Superviser sup = getSuperviserOnChain(id);
            jsonObj.put("role", Constant.ROLE_TYPE_SUPERVISER);
            jsonObj.put("user", sup);
        }else if (role == Constant.ROLE_TYPE_BUSSINESS){
            Business bus = getBussinessOnChain(id);
            jsonObj.put("role", Constant.ROLE_TYPE_BUSSINESS);
            jsonObj.put("user", bus);
        }else if (role == Constant.ROLE_TYPE_CUSTOMER){
            Customer cus = getCustomerOnChain(id);
            jsonObj.put("role", Constant.ROLE_TYPE_CUSTOMER);
            jsonObj.put("user", cus);
        }
        return jsonObj;
   
    }

    @Override
    public Boolean commodityCreate(String name, String price, String issuer, String issuerName) {
        return mapper.createCommodity(name, price, issuer, issuerName);
    }

    @Override
    public List<Commodity> getCommodityOnSaleByIssuer(String issuer) {
        return mapper.getCommodityOnSaleByIssuer(issuer);
    }

    @Override
    public List<Commodity> getCommodityOnSale() {
        return mapper.getCommodityOnSale();
    }

    @Override
    public Boolean createBuyTrade(String tTime, String price, String bId, String sId, String cId) {
        return mapper.createBuyTrade(tTime, price, bId, sId, cId);
    }

    @Override
    public List<Commodity> getCommodityBaughtByCus(String customer) {
        return mapper.getCommodityBaughtByCus(customer);
    }

    @Override
    public List<Commodity> getCommoditySaledByBus(String issuer) {
        return mapper.getCommoditySaledByIssuer(issuer);
    }

    


    
}
