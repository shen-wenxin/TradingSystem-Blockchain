package com.example.tradingSystem.web.controller;

import java.util.List;

import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.Commodity.Commodity;
import com.example.tradingSystem.domain.Commodity.SaleCommodity;
import com.example.tradingSystem.service.BlockChainService;
import com.example.tradingSystem.web.exception.BussinessException;
import com.example.tradingSystem.web.exception.JsonResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping(value = "/commodity")   
public class CommodityController {

    @Autowired
    private BlockChainService blockchainService;

    // 上架商品
    @PostMapping("/sale")
    public JsonResult sale(@RequestBody SaleCommodity sCommodity){

        log.info("[SALE]Begin to sale goods");

        String issuerId = sCommodity.getIssuerId();
        String issuerName = sCommodity.getIssuerName();
        String name = sCommodity.getName();
        String price = sCommodity.getPrice();

        // 检查该用户是否在链上
        // TODO: 这个权限校验需要更改
        if(! blockchainService.userExist(issuerId)){
            log.info("链上不存在该用户的数据");
            throw new BussinessException(Status.FAIL_NO_PERMISSION.code());
        }

        Boolean result = blockchainService.commodityCreate(name, price, issuerId, issuerName);

        if (!result){
            //failed
            log.error("Sale goods failed");
            throw new BussinessException(Status.FAIL_OPERATION.code());
        }else{
            JsonResult res = new JsonResult();
            res.success();
            return res;
        }
    }

    // 获取该商家已上架且未售出的商品
    @GetMapping("/getOnSaleGoodInfo/{id}")
    public JsonResult getSaleGoodInfo(@PathVariable String id) throws Exception{
        log.info("[getSaleGoodInfo] begin");
        List<Commodity> com = blockchainService.getCommodityOnSaleByIssuer(id);
        JsonResult result = new JsonResult();
        result.ok(com);
        return result;
    }

    // 获取所有已上架的商品
    @GetMapping("/getAllOnSaleGoodInfo")
    public JsonResult getAllSaleGoodInfo() throws Exception{
        log.info("[getAllSaleGoodInfo] begin");
        List<Commodity> coms = blockchainService.getCommodityOnSale();
        JsonResult result = new JsonResult();
        result.ok(coms);
        return result;
    }

    // 获取该买家所有已购买的商品
    @GetMapping("/getGoodBeBaught/{id}")
    public JsonResult getGoodBeBaughtbyCus(@PathVariable String id) throws Exception{
        log.info("[getGoodBeBaughtbyCus] Begin and the id is " + id);
        List<Commodity> coms = blockchainService.getCommodityBaughtByCus(id);
        JsonResult result = new JsonResult();
        result.ok(coms);
        return result;
    }

    @GetMapping("/getGoodSaled/{id}")
    public JsonResult getGoodSaledbyBus(@PathVariable String id) throws Exception {
        log.info("[getGoodSaledbyBus] Begin and the id is " + id);
        List<Commodity> coms = blockchainService.getCommoditySaledByBus(id);
        JsonResult result = new JsonResult();
        result.ok(coms);
        return result;
    }


}
