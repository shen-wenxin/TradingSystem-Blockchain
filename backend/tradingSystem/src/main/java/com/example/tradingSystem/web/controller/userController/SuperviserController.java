package com.example.tradingSystem.web.controller.userController;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import com.example.tradingSystem.TradingSystemApplication;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.web.exception.BussinessException;
import com.example.tradingSystem.web.exception.JsonResult;

@Slf4j
@RestController
@RequestMapping(value = "/users/superviser")     // 通过这里配置使下面的映射都在/users下
public class SuperviserController { 
    /**
     * 处理/users/superviser/getAll 的get请求，用来获取superviser列表
     * @throws ContractException
     */
    @GetMapping("/getAll")
    public JsonResult getSuperviserList() throws Exception{
        try{
            log.info("Begin to query all supervisers.");

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

    /**
     * 处理/users/superviser/register 的post请求，用来创建superviser
     */
    @PostMapping("/register")
    public JsonResult registerSuperviser(@RequestBody Superviser superviser) throws Exception{
        // @RequestBody注解用来绑定通过http请求中application/json类型上传的数据
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

    /**
     * 处理/users/superviser/get/{id} 的get请求，用来获取url中id值的superviser信息
     */
    @GetMapping("/get/{id}")
    public JsonResult getSuperviser(@PathVariable String id){
        // url 中的 id 可以通过@PathVariable 绑定到函数中
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

    /**
     * 处理/users/superviser/delete/{id} 的delete请求，删除url中id值的superviser信息
     */
    @DeleteMapping("/delete/{id}")
    public JsonResult deleteSuperviser(@PathVariable String id){
        // url 中的 id 可以通过@PathVariable 绑定到函数中
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
