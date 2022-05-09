package com.example.tradingSystem.domain.Trade;

import lombok.Data;

@Data
public class Buy {

    private String tradeTime; // 交易时间

    private String price;   // 交易价格

    private String buyerId; // 买家id号

    private String salerId; //卖家id号

    private String commodityId; // 商品id号

}
