package com.example.tradingSystem.web.controller;

import java.util.List;

import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.Trade.Buy;
import com.example.tradingSystem.domain.Trade.Trade;
import com.example.tradingSystem.service.BlockChainService;
import com.example.tradingSystem.web.exception.BussinessException;
import com.example.tradingSystem.web.exception.JsonResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/trade")
public class TradeController {

    @Autowired
    private BlockChainService blockchainService;

    @PostMapping("/buy")
    public JsonResult buy(@RequestBody Buy buydata) throws Exception{
        log.info("[trade]Init trade buy.");

        String tTime = buydata.getTradeTime();
        String price = buydata.getPrice();
        String buyerId = buydata.getBuyerId();
        String salerId = buydata.getSalerId();
        String commodityId = buydata.getCommodityId();

        // 判断参数是否为空

        if(tTime.isEmpty() || price.isEmpty() || buyerId.isEmpty() || salerId.isEmpty() || commodityId.isEmpty()){
            log.debug("[trade] params is invalid");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        
        // 发生交易
        Boolean result = blockchainService.createBuyTrade(tTime, price, buyerId, salerId, commodityId);
        
        if (!result){
            // failed
            log.error("buy failed");
            throw new BussinessException(Status.FAIL_OPERATION.code());
        }else{
            JsonResult res = new JsonResult();
            res.success();
            return res;
        }

    }

    // 获取所有交易信息
    @GetMapping("/getAllTrade")
    public JsonResult getAllTradeInfo() throws Exception{
        log.info("[getAllTrade]getAllTrade");
        List<Trade> res = blockchainService.getAllTrades();
        JsonResult result = new JsonResult();
        result.ok(res);
        return result;
    }

}
