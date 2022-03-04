package main

import (
    "errors"
    "fmt"

    "github.com/hyperledger/fabric-contract-api-go/contractapi"
)


// ========== data saved on chain ==========

// t_user_account
type User struct{
    AccountId       string `json:"accountId"` 
    DiscountList    string `json:"discountList"`
    CommodityIdList string `json:"commodityIdList"`
    Balance         string `json:"balance"`
    Currency        string `json:"currency"`
    LastUpdateTime  string `json:"lastUpdateTime"`
}

// t_bussiness_account
type Bussiness struct{
    AccountId       string `json:"accountId"` 
    CommodityIdList string `json:"commodityIdList"`
    Balance         string `json:"balance"`
    Currency        string `json:"currency"`
    DiscountList    string `json:"discountList"`
    LastUpdateTime  string `json:"lastUpdateTime"`
}

// t_commodity
type Commmodity struct{
    CommodityId     string `json:"commodityId"` 
    Name            string `json:"name"` 
    Price           string `json:"price"` 
    Currency        string `json:"currency"`
    IssuerId        string `json:"issuerId"`
    OwnerId         string `json:"ownerId"`
    LastUpdate      string `json:"lastUpdate"`
    State           string `json:"state"`
    Remakes	        string `json:"remakes"`
}

// t_trade
type Trade struct{
    TradeId         string `json:"tradeId"` 
    TradeTime       string `json:"tradeTime"` 
    BuyerId         string `json:"buyerId"` 
    SalerId         string `json:"salerId"` 
    CommodityId     string `json:"commodityId"` 
    Valid           bool   `json:"valid"` 
    LastUpdateTime  string `json:"lastUpdateTime"`
    Remakes	        string `json:"remakes"`
}

// t_operate
type Operate struct{
    Id              string `json:"tradeId"` 
    Time            string `json:"time"` 
    OperaterId      string `json:"operaterId"` 
    OperatereedId   string `json:"operatereedId"` 
    OperateType     string `json:"operateType"` 
}


// t_discount
type Discount struct{
    DiscountId      string `json:"discountId"` 
    Name            string `json:"name"`
    AccountId       string `json:"accountId"`
    BusinessId      string `json:"businessId"`
    BeginTime       string `json:"beginTime"`
    EndTime         string `json:"endTime"`
    Valid           bool   `json:"valid"`
}
// ====================================

// ================= FORMAT =================
// currency code
const (
    CURRENCY_RMB = "0001"
)

// commodity state code
const (
    COMMMODITY_STATE_ONSALE     = "0001"
    COMMMODITY_STATE_OFFSATE    = "0002"
    COMMMODITY_STATE_BEBOUGHT   = "0003"
    COMMMODITY_STATE_RETURNED   = "0004"
)
// remarks for t_trade
const (
    REMAKES_TRADE_BOUGHT        = "Be bought"
    REMAKES_TRADE_RETURN        = "returned"
)



// SimpleContract contract for handling writing and reading from the world state
type SimpleContract struct {
    contractapi.Contract
}
