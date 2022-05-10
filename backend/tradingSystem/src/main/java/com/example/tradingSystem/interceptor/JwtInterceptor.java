package com.example.tradingSystem.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.tradingSystem.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        System.out.println("get in preHandle");

        //从 http 请求头中取出 token
        String token = request.getHeader("token");
        System.out.println("get token：" + token);

        if (token == null) {
            throw new RuntimeException("no token ，please login again");
        }

        //验证 token
        JwtUtil.checkSign(token);

        //验证通过后， 这里测试取出JWT中存放的数据
        //获取 token 中的 userId
        String userId = JwtUtil.getUserId(token);
        System.out.println("id : " + userId);

        return true;
    }

    
}
