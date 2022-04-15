package com.example.tradingSystem.web.UserController;

import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.example.tradingSystem.domain.User.Superviser;


@RestController
@RequestMapping(value = "/users/superviser")     // 通过这里配置使下面的映射都在/users下
public class SuperviserController {

    // 创建线程安全的Map, 模拟superviser信息的存储（后续请删除）
    static Map<String, Superviser> supervisers = Collections.synchronizedMap(new HashMap<String, Superviser>());
    
    /**
     * 处理/users/superviser/getAll 的get请求，用来获取superviser列表
     */
    @GetMapping("/getAll")
    public List<Superviser> getSuperviserList(){
        List<Superviser> superviserList = new ArrayList<Superviser>(supervisers.values());
        return superviserList;
    }

    /**
     * 处理/users/superviser/register 的post请求，用来创建superviser
     */
    @PostMapping("/register")
    public String registerSuperviser(@RequestBody Superviser superviser){
        // @RequestBody注解用来绑定通过http请求中application/json类型上传的数据
        supervisers.put(superviser.getId(), superviser);
        return "success" ;
    }

    /**
     * 处理/users/superviser/get/{id} 的get请求，用来获取url中id值的superviser信息
     */
    @GetMapping("/get/{id}")
    public Superviser getSuperviser(@PathVariable String id){
        // url 中的 id 可以通过@PathVariable 绑定到函数中
        return supervisers.get(id);
    }

    /**
     * 处理/users/superviser/delete/{id} 的delete请求，删除url中id值的superviser信息
     */
    @DeleteMapping("/delete/{id}")
    public String deleteSuperviser(@PathVariable String id){
        supervisers.remove(id);
        return "success";
    }


















    
}
