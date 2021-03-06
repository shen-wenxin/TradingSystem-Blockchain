package com.example.tradingSystem.domain.User;



import java.util.List;

import lombok.Data;

@Data
public class Customer {

    // 消费者ID 全局唯一
    private String id;

    // 监管机构名称
    private String name;

    // 电话号码
    private String phone;

    // 优惠卷id List
    private List<String> discountList;

    // 商品Id list
    private List<String> commodityIdList;

    // 购买商品个数
    private Integer commodityCount;  

    // 余额 单位：分
    private Integer balance;

    // 币种
    private String currency;

    // 账户状态
    private Boolean state;

    // 上次更新时间
    private String lastUpdateTime;

}
