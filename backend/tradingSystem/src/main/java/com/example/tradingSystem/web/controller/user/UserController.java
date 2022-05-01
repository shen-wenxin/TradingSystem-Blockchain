package com.example.tradingSystem.web.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.service.TokenService;
import com.example.tradingSystem.service.user.SuperviserService;
import com.example.tradingSystem.service.user.UserService;
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
@RequestMapping(value = "/usr")     
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SuperviserService superviserService;

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
        }

        // 判断区块链上是否已经有该用户的数据
        










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
            log.info("用户[" + account + "]登录认证通过");
        }else{
            throw new BussinessException(Status.ACCOUNT_PASSWORD_ERROR.code());
        }

        // 登录成功 生成token
        String token = tokenService.getToken(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        JsonResult result = new JsonResult();
        result.ok(jsonObject);
        log.info("[LOGIN]Login succeed.");
        return result;
    }

    @GetMapping("/getUserInfo/{id}")
    public JsonResult getUserInfo(@PathVariable String id) throws Exception{
        log.info("[GETUSERINFO]Begin to get User Info");
        User userData = userService.getUser(id);
        log.info("account :{} role is {}", userData.getAccount(), userData.getRole());
        Integer role = userData.getRole();
        if (role == Constant.ROLE_TYPE_SUPERVISER){
            Superviser sup = superviserService.getSuperviserOnChain(id);

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("role", Constant.ROLE_TYPE_SUPERVISER);
            jsonObj.put("user", sup);
            JsonResult result = new JsonResult();
            result.ok(jsonObj);
            return result;

        }else{
            JsonResult result2 = new JsonResult();
            result2.ok("error");
            return result2;
        }
    }


}
