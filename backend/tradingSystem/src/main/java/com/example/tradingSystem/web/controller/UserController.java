package com.example.tradingSystem.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.Commodity.Commodity;
import com.example.tradingSystem.domain.Trade.Account;
import com.example.tradingSystem.domain.Trade.Trade;
import com.example.tradingSystem.domain.User.Business;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.service.BlockChainService;
import com.example.tradingSystem.service.UserService;
import com.example.tradingSystem.util.JwtUtil;
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
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private BlockChainService blockchainService;

    @Autowired
    private HttpServletRequest  request;


    // 注册
    @PostMapping("/register")
    public JsonResult register(@RequestBody User user) throws Exception{
        log.info("[REGISTER]Begin to register.");

        String account = user.getAccount();
        String name = user.getName();
        String password = user.getPassword();
        Integer role = user.getRole();

        // 判断参数是否为空
        if (account.isEmpty() || name.isEmpty() || password.isEmpty() || role == null){
            log.debug("[REGISTER] params is invalid");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }

        // 判断数据库里是否已经有该用户的数据
        User userBase = userService.getUser(account);
        if (userBase != null){
            log.debug("[REGISTER] user is exist");
            throw new BussinessException(Status.USER_DATA_EXIST.code());
        }
       
        // 判断区块链上是否已经有该用户的数据
        if (blockchainService.userExist(account, role)){
            log.debug("[REGISTER] user is exist");
            throw new BussinessException(Status.USER_DATA_EXIST.code());
        }

        // 链上数据注册
        Boolean result = blockchainService.userRegister(user);

        // mysql 数据库数据注册
        userService.insertUser(user);

        if (!result){
            // failed
            log.error("Register failed");
            throw new BussinessException(Status.FAIL_OPERATION.code());
        }else{
            JsonResult res = new JsonResult();
            res.success();
            return res;
        }
    }
    
    
    // 登录
    @PostMapping("/login")
    public JsonResult login(@RequestBody User user) throws Exception{
        // 校验用户名密码是否一致
        log.info("[LOGIN]Begin to login");
        String account = user.getAccount();
        String password = user.getPassword();

        User userData = userService.getUser(account);
        // 用户不存在
        if (userData == null){
            throw new BussinessException(Status.ACCOUNT_PASSWORD_ERROR.code());
        }
        // 校验 
        if (password.equals(userData.getPassword())){
            log.info("user[" + account + "]password");
        }else{
            throw new BussinessException(Status.ACCOUNT_PASSWORD_ERROR.code());
        }

        // 登录成功 生成token
        String token = JwtUtil.sign(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        JsonResult result = new JsonResult();
        result.ok(jsonObject);
        log.info("[LOGIN]Login succeed.");
        return result;
    }

    @PostMapping("/logout")
    public JsonResult logout() throws Exception{

        // 暂时没想好要干啥
        JsonResult result = new JsonResult();
        result.success();
        return result;
    }

    @GetMapping("/usr/getUserInfo")
    public JsonResult getUserInfo() throws Exception{
        String token = request.getHeader("token");
        log.info("[GETUSERINFO]Begin to get User Info");

        String id = JwtUtil.getUserId(token);
        log.info("token is"+ token);
        User userData = userService.getUser(id);
        log.info("account :{} role is {}", userData.getAccount(), userData.getRole());
        
        String account = userData.getAccount();
        Integer role = userData.getRole();
        JSONObject jsonObj = blockchainService.getUserInfoOnChain(account, role);

        JsonResult result = new JsonResult();
        result.ok(jsonObj);
        return result;
    }

    @GetMapping("/usr/getUserNameById/{id}")
    public JsonResult getUserNameById(@PathVariable String id) throws Exception{
        log.info("[getUserNameById]Begin to getUserNameById");
        String name = blockchainService.getUserNameById(id);
        JsonResult result = new JsonResult();
        result.ok(name);
        return result;
    }

    // 通过bid 获取business 信息
    @GetMapping("/usr/getBusInfo/{id}")
    public JsonResult getBusInfoById(@PathVariable String id) throws Exception{
        log.info("[getBusInfoById]Begin to get Bus Info");
        Business bus = blockchainService.getBussinessOnChainById(id);
        log.info("bus information, balance"+bus.getBalance());
        JsonResult result = new JsonResult();
        result.ok(bus);
        return result;
    }

    // 获取该商家该月的收益
    @GetMapping("/usr/getBusMonthProfit/{id}")
    public JsonResult getBusMonthProfitById(@PathVariable String id) throws Exception{
        log.info("[getBusMonthProfitById]getBusMonthProfit");
        Integer res = blockchainService.getBusProfitByMonth(id);
        JsonResult result = new JsonResult();
        result.ok(res);
        return result;
    }

    @GetMapping("/usr/getUserAccountList/{id}")
    public JsonResult getBusAccountList(@PathVariable String id) throws Exception{
        log.info("[getBusAccountList]getBusAccountList begin");
        List<Account> accoutList = blockchainService.getUserAccountList(id);
        JsonResult result = new JsonResult();
        result.ok(accoutList);
        return result;
    }

    // 通过 cId 获取 Cus 信息
    @GetMapping("/usr/getCusInfo/{id}")
    public JsonResult getCusInfoById(@PathVariable String id) throws Exception{
        log.info("[getCusInfoById]Begin to get Bus Info");
        Customer cus = blockchainService.getCustomerOnChianById(id);
        JsonResult result = new JsonResult();
        result.ok(cus);
        return result;
    }

    // 获取该消费者该月的消费
    @GetMapping("/usr/getCusMonthSpent/{id}")
    public JsonResult getCusMonthSpentById(@PathVariable String id) throws Exception{
        log.info("[getCusMonthSpentById]getCusMonthSpentById");
        Integer res = blockchainService.getCusSpentByMonth(id);
        JsonResult result = new JsonResult();
        result.ok(res);
        return result;
    }

    // 获取所有商家信息
    @GetMapping("/usr/getAllBusInfo")
    public JsonResult getAllBusInfo() throws Exception{
        log.info("[getAllBusInfo]getAllBusInfo");
        List<Business> res = blockchainService.getAllBusiness();
        JsonResult result = new JsonResult();
        result.ok(res);
        return result;
    }

    // 获取所有消费者信息
    @GetMapping("/usr/getAllCusInfo")
    public JsonResult getAllCusInfo() throws Exception{
        log.info("[getAllCusInfo]getAllCusInfo");
        List<Customer> res = blockchainService.getAllCustomer();
        JsonResult result = new JsonResult();
        result.ok(res);
        return result;
    }

    

    














}
