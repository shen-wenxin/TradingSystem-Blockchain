package com.example.tradingSystem.service.useService;

import com.example.tradingSystem.TradingSystemApplication;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.web.exception.BussinessException;
import com.example.tradingSystem.web.exception.JsonResult;

import org.hyperledger.fabric.gateway.Contract;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerService {

    public static JsonResult getCustomerListOnChain(){
        try{
            log.info("Begin to query all Customers on chain.");

            log.info("[Fabric]Evaluate Transaction: QueryAllCustomer");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            String result = new String(contract.evaluateTransaction("QueryAllCustomer"));

            log.info("[Fabric]Evaluate succeed."+ new String(result));
            JsonResult JsonResult = new JsonResult();
            JsonResult.ok(result);
            return JsonResult;
        }catch (Exception e){
            log.error("Query all Customers failed.");
            log.error(e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code()); 
        }

    }

    public static JsonResult registeCustomerOnChain(Customer customer){
        log.info("Begin to customer register");
        String id = customer.getAccountId();
        String name = customer.getName();
        String phone = customer.getPhone();

        // 不空判断
        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()){
            log.error("parm is invalid.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());  
        }
        // id 加入Customer的前缀
        id = Constant.PREFIX_ID_CUSTOMER + id;
        try{
            // 调用链代码，完成注册
            log.info("[Fabric]Submit Transaction: CreateCustomer");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            contract.submitTransaction("CreateCustomer", id, name, phone);
            log.info("[Fabric]CreateCustomer Succeed.");

            // 注册成功
            JsonResult JsonResult = new JsonResult();
            JsonResult.success();
            return JsonResult; 
        }catch (Exception e){
            log.error("[Fabric]CreateCustomer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    public static JsonResult getCustomerFromChain(String id){
        log.info("Begin to get customer details. id: " + id);

        if (id.isEmpty()){
            log.error("id cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }

        // id 加上 Customer 的前缀
        id = Constant.PREFIX_ID_CUSTOMER + id;

        try{
            log.info("[Fabric]Evaluate Transaction: QueryCustomer");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            String result = new String(contract.evaluateTransaction("QueryCustomer", id));
            log.info("[Fabric]QueryCustomer Succeed.");

            JsonResult JsonResult = new JsonResult();
            JsonResult.ok(result);
            return JsonResult;
        }catch (Exception e){
            log.error("[Fabric]QueryCustomer falied." + e.getMessage());
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
        id = Constant.PREFIX_ID_CUSTOMER + id;

        try{

            log.info("[Fabric]Evaluate Transaction: DeleteCustomer");
            Contract contract = TradingSystemApplication.getFabricService().getContract();
            contract.submitTransaction("DeleteCustomer", id);
            log.info("[Fabric]DeleteCustomer Succeed.");

            JsonResult JsonResult = new JsonResult();
            JsonResult.success();
            return JsonResult;




        }catch (Exception e){
            log.error("[Fabric]DeleteCustomer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());


        }







    }









    
    
}
