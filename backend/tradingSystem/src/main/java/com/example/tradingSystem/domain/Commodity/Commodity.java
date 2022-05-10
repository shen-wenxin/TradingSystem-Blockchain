package com.example.tradingSystem.domain.Commodity;

import lombok.Data;

@Data
public class Commodity {

    private String id; // id 号
    
    private String name; // 名字

    private String price; // 价格

    private String currency; // 币种

    private String issuerId; // 发行者id

    private String issuerName; // 发行者名称

    private String ownerId; // 所属者id

    private String ownerName; // 所属者名称

    private String lastUpdate; // 上次更新时间

    private String State; // 状态

    
}
