package com.example.tradingSystem.service.impl;

import java.util.List;

import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.entry.blockchain.BlockChainMapper;
import com.example.tradingSystem.entry.blockchain.BlockChainMapperImpl;
import com.example.tradingSystem.service.user.SuperviserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuperviserServiceImpl implements SuperviserService{

    @Autowired 
    private BlockChainMapper mapper;

    // @Override
    // public List<Superviser> getSuperviserListOnChain() {
    //     // TODO Auto-generated method stub
    //     return null;
    // }

    @Override
    public Superviser getSuperviserOnChain(String id) {
        return mapper.getSuperviser(id);
    }
    
}
