package com.example.tradingSystem.domain.User;

import lombok.Data;

@Data
public class Superviser {

    // 监管机构ID 全局唯一
    private String id;

    // 监管机构名称
    private String name;

    // 账户状态 是否有效
    private boolean state;

    // 备注
    private String remarks;

    // 上次更新时间 时间戳
    private String lastUpdateTime;
    
}
