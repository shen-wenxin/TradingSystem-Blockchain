package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// t_trade
type Trade struct {
	Id             string `json:"tradeId"`        // 交易id号
	TradeTime      string `json:"tradeTime"`      // 交易时间
	Price          int64  `json:"price"`          // 价格
	BuyerId        string `json:"buyerId"`        // 消费者id
	SalerId        string `json:"salerId"`        //商家id
	CommodityId    string `json:"commodityId"`    // 商品id
	Valid          bool   `json:"valid"`          // 交易是否有效
	LastUpdateTime string `json:"lastUpdateTime"` // 最近更新时间
}

// 产生购买交易
func (s *SmartContract) CreateBuyTrade(ctx contractapi.TransactionContextInterface,
	tId string, price string, bId string, sId string, cId string) error {

	// 前缀校验
	if (!strings.HasPrefix(tId, PREFIX_ID_TRADE)) ||
		(!strings.HasPrefix(bId, PREFIX_ID_CUSTOMER)) ||
		(!strings.HasPrefix(sId, PREFIX_ID_BUSSINIESSMAN)) ||
		(!strings.HasPrefix(cId, PREFIX_ID_COMMODITY)) {

		return fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	// 在create之前，判断id 值是否已经被使用
	flag, _ := s.idOnChainCheck(ctx, tId)
	if flag {
		return fmt.Errorf(ERROR_CODE_EXISTINGDATA)
	}

	pri, _ := strconv.Atoi(price)

	tTime := strconv.FormatInt(time.Now().Unix(), 10)
	trade := Trade{
		Id:             tId,
		TradeTime:      tTime,
		Price:          int64(pri),
		BuyerId:        bId,
		SalerId:        sId,
		CommodityId:    cId,
		Valid:          true,
		LastUpdateTime: tTime,
	}

	tradeAsBytes, _ := json.Marshal(trade)
	err := ctx.GetStub().PutState(trade.Id, tradeAsBytes)

	if err != nil {
		return fmt.Errorf(ERROR_CODE_PUTCHAINFAILED)
	}

	// 开始创建复合键

	err = s.createCompositeKeyandSave(ctx, "buyerId~id", []string{trade.BuyerId, trade.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, "saler~id", []string{trade.SalerId, trade.Id})
	if err != nil {
		return err
	}

	// 修改commodity 状态
	err = s.ChangeCommodityBeBaught(ctx, cId, sId, bId, tTime)
	if err != nil {
		return err
	}

	return nil
}
