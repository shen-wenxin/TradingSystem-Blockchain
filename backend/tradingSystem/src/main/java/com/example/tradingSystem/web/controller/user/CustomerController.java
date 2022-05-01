package com.example.tradingSystem.web.controller.user;

import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.service.user.CustomerService;
import com.example.tradingSystem.web.exception.JsonResult;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController

@RequestMapping(value = "/users/customer")     // 通过这里配置使下面的映射都在/users下
public class CustomerController {

    @GetMapping("/getAll")
    public JsonResult getSuperviserList() throws Exception{
        return CustomerService.getCustomerListOnChain();
        
    }


    @PostMapping("/register")
    public JsonResult registerSuperviser(@RequestBody Customer customer) throws Exception{
        return CustomerService.registeCustomerOnChain(customer);

        
    }

    @GetMapping("/get/{id}")
    public JsonResult getSuperviser(@PathVariable String id){
        // url 中的 id 可以通过@PathVariable 绑定到函数中
        return CustomerService.getCustomerFromChain(id);
     
    }

    @DeleteMapping("/delete/{id}")
    public JsonResult deleteSuperviser(@PathVariable String id){
        return CustomerService.deleteSuperviserOnChain(id);
       
    }

















    
}
