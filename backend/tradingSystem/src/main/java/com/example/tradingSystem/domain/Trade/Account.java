package com.example.tradingSystem.domain.Trade;

import lombok.Data;

@Data
public class Account {

    //账目id号
    private String Id;

    // 该账目所属用户id号
    private String userId; 

    // 月份
    private String month;

    // 年份
    private String year;

    // 收入 
    private Integer income;

    // 支出
    private Integer outcome;

    // 总共
    private Integer total;


    


    
}
