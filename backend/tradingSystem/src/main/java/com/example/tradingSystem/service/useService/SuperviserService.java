package com.example.tradingSystem.service.useService;

import com.example.tradingSystem.TradingSystemApplication;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.web.exception.BussinessException;
import com.example.tradingSystem.web.exception.JsonResult;

import org.hyperledger.fabric.gateway.Contract;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuperviserService {

    public static JsonResult getSuperviserListOnChain(){
        try{
            log.info("Begin to query all supervisers on chain.");

            log.info("[Fabric]Evaluate Transaction: QueryAllSuperviser");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            String result = new String(contract.evaluateTransaction("QueryAllSuperviser"));
            log.info("[Fabric]Evaluate succeed."+ new String(result));

            JsonResult JsonResult = new JsonResult();
            JsonResult.ok(result);
            return JsonResult;

        }catch (Exception e){
            log.error("Query all superviser failed.");
            log.error(e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code(), e.getMessage());
        }

    }

    public static JsonResult registerSuperviserOnChain(Superviser superviser){
        log.info("Begin to superviser register");
        String id = superviser.getId();
        String name = superviser.getName();
        String remarks = superviser.getRemarks();

        //id name 不空判断
        if(id.isEmpty() || name.isEmpty()){
            log.error("parm is invalid.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        //id 加入SuperViser的前缀
        id = Constant.PREFIX_ID_SUPERVISER + id;
        try{
            //调用链代码，完成注册
            log.info("[Fabric]Submit Transaction: CreateSuperviser");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            contract.submitTransaction("CreateSuperviser", id, name, remarks);
            log.info("[Fabric]CreateSuperviser Succeed.");
            // 注册成功
            JsonResult JsonResult = new JsonResult();
            JsonResult.success();
            return JsonResult;  

        }catch (Exception e){
            log.error("[Fabric]CreateSuperviser falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        } 
    }

    public static JsonResult getSuperviserFromChain(String id){
        log.info("Begin to get superviser details. id: " + id);
        if (id.isEmpty()){
            log.error("id cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }

        //id 加入SuperViser的前缀
        id = Constant.PREFIX_ID_SUPERVISER + id;

        try{
            log.info("[Fabric]Evaluate Transaction: QuerySuperviser");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            String result = new String(contract.evaluateTransaction("QuerySuperviser", id));
            log.info("[Fabric]QuerySuperviser Succeed.");
            JsonResult JsonResult = new JsonResult();
            JsonResult.ok(result);
            return JsonResult;

        }catch (Exception e){
            log.error("[Fabric]QuerySuperviser falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    public static JsonResult deleteSuperviserOnChain(String id){
        
        log.info("Begin to delete superviser. id: " + id);
        if (id.isEmpty()){
            log.error("id cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }

        //id 加入SuperViser的前缀
        id = Constant.PREFIX_ID_SUPERVISER + id;

        try{
            log.info("[Fabric]Evaluate Transaction: DeleteSuperviser");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            contract.submitTransaction("DeleteSuperviser", id);
            log.info("[Fabric]DeleteSuperviser Succeed.");
            JsonResult JsonResult = new JsonResult();
            JsonResult.success();
            return JsonResult;

        }catch (Exception e){
            log.error("[Fabric]DeleteSuperviser falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }
}
