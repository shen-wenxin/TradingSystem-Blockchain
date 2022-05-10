package com.example.tradingSystem.domain.Trade;

import lombok.Data;

@Data
public class Trade {

    private String tradeId; //交易id号

    private String tradeTime; // 交易时间(时间戳格式)

    private String tradeDay; // 交易当前日

    private String tradeMonth; //交易月份

    private String tradeYear; //交易年份

    private Integer price; //价格

    private String buyerId; // 消费者id

    private String salerId; // 商家id

    private Boolean valid; //交易是否有效

    private String lastUpdateTime; // 最新更新时间
    
}
