package com.example.tradingSystem.service.impl;

import java.util.List;

import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.domain.User.Business;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.entry.blockchain.BlockChainMapper;
import com.example.tradingSystem.entry.blockchain.BlockChainMapperImpl;
import com.example.tradingSystem.service.BlockChainService;

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
    
}
