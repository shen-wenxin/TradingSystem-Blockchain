// package com.example.tradingSystem.web.controller.user;

// import org.hyperledger.fabric.gateway.ContractException;
// import org.springframework.web.bind.annotation.*;

// import lombok.extern.slf4j.Slf4j;
// import com.example.tradingSystem.domain.User.Superviser;
// import com.example.tradingSystem.service.user.SuperviserService;
// import com.example.tradingSystem.web.exception.JsonResult;

// @Slf4j
// @RestController
// @RequestMapping(value = "/users/superviser")     // 通过这里配置使下面的映射都在/users下
// public class SuperviserController { 
//     /**
//      * 处理/users/superviser/getAll 的get请求，用来获取superviser列表
//      * @throws ContractException
//      */
//     @GetMapping("/getAll")
//     public JsonResult getSuperviserList() throws Exception{
//         return SuperviserService.getSuperviserListOnChain();
//     }

//     /**
//      * 处理/users/superviser/register 的post请求，用来创建superviser
//      */
//     @PostMapping("/register")
//     public JsonResult registerSuperviser(@RequestBody Superviser superviser) throws Exception{
//         // @RequestBody注解用来绑定通过http请求中application/json类型上传的数据
//         return SuperviserService.registerSuperviserOnChain(superviser);
//     }

//     /**
//      * 处理/users/superviser/get/{id} 的get请求，用来获取url中id值的superviser信息
//      */
//     @GetMapping("/get/{id}")
//     public JsonResult getSuperviser(@PathVariable String id){
//         // url 中的 id 可以通过@PathVariable 绑定到函数中
//         return SuperviserService.getSuperviserFromChain(id);
//     }


//     /**
//      * 处理/users/superviser/delete/{id} 的delete请求，删除url中id值的superviser信息
//      */
//     @DeleteMapping("/delete/{id}")
//     public JsonResult deleteSuperviser(@PathVariable String id){
//         return SuperviserService.deleteSuperviserOnChain(id);
//     }


















    
// }
