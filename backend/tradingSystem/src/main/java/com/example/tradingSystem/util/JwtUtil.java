package com.example.tradingSystem.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.web.exception.BussinessException;

import java.util.Date;
import java.util.Map;


public class JwtUtil {
    /**
     * 过期5分钟
     * */
    private static final long EXPIRE_TIME = 60 * 60 * 1000;

    /**
     * jwt密钥
     * */
    private static final String SECRET = "jwt_secret";

    /**
     * 生成jwt字符串，一小时后过期  JWT(json web token)
     * @param userId
     * @param 
     * @return
     * */
    public static String sign(User user) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    //将userId保存到token里面
                    .withAudience(user.getAccount())
                    //存放自定义数据
                    .withClaim("info", user.getPassword())
                    //五分钟后token过期
                    .withExpiresAt(date)
                    //token的密钥
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据token获取userId
     * @param token
     * @return
     * */
    public static String getUserId(String token) {
        try {
            String userId = JWT.decode(token).getAudience().get(0);
            return userId;
        }catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 根据token获取自定义数据info
     * @param token
     * @return
     * */
    public static String getInfo(String token) {
        try {
            return JWT.decode(token).getClaim("info").asString();
        }catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 校验token
     * @param token
     * @return
     * */
    public static boolean checkSign(String token) {
        try {
            Algorithm algorithm  = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    //.withClaim("username, username)
                    .build();
            verifier.verify(token);
            return true;
        }catch (JWTVerificationException e) {
            throw new BussinessException(Status.FAIL_INVALID_TOKEN.code());
        }
    }
}
