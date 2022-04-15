package com.example.tradingSystem.domain.User;
import java.util.List;

import lombok.Data;


@Data
public class Bussiness {

    // 商户id 全局唯一
    private String accountId;

    // 商品Id list
    private List<String> commodityIdList;

    // 余额 单位：分
    private Integer balance;

    // 币种
    private String currency;

    // 商家派发的优惠卷的List
    private List<String> discountList;

    // 账户状态
    private Boolean state;

    // 上次更新时间
    private String lastUpdateTime;
  
}
