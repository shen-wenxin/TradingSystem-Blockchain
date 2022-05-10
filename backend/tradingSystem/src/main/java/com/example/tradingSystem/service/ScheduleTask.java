package com.example.tradingSystem.service;

import java.util.Calendar;
import java.util.Date;

import com.example.tradingSystem.entry.blockchain.BlockChainMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ScheduleTask {

    @Autowired 
    private BlockChainMapper mapper;
    
    @Scheduled(cron = "0 10 0 1 * ?")//每月1号的0:10:00执行
    public void monthlyAccountCheck(){
        log.info("begin to monthly account check.");

        
        mapper.monthlyAccountCheck();
        log.info("monly account check finish");

    }


    
}
