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
	TradeTime      string `json:"tradeTime"`      // 交易时间(时间戳格式存储)
	TradeDay       string `json:"TradeDay"`       // 交易当前日
	TradeMonth     string `json:"tradeMonth"`     // 交易月份
	TradeYear      string `json:"tradeYear"`      // 交易年份
	Price          int64  `json:"price"`          // 价格
	BuyerId        string `json:"buyerId"`        // 消费者id
	SalerId        string `json:"salerId"`        //商家id
	CommodityId    string `json:"commodityId"`    // 商品id
	Valid          bool   `json:"valid"`          // 交易是否有效
	LastUpdateTime string `json:"lastUpdateTime"` // 最近更新时间
}

const(
	INDEXNAME_SALER_MONTH_YEAR_TRADE = "saler~month~year~tid"
	INDEXNAME_SALER_YEAR_TRADE = "salerId~year~tId"
	INDEXNAME_SALER_TRADE = "saler~id"

	INDEXNAME_BUYER_MONTH_YEAR_TRADE = "buyerId~month~year~tId"
	INDEXNAME_BUYER_YEAR_TRADE = "buyerId~year~tId"
	INDEXNAME_BUYER_TRADE = "buyerId~id"



)

// 产生购买交易
func (s *SmartContract) CreateBuyTrade(ctx contractapi.TransactionContextInterface,
	tId string, tradeTime string, price string, bId string, sId string, cId string) error {
	pri, _ := strconv.Atoi(price)

	// 更改cus状态
	err := s.ReduceCustomerBalance(ctx, bId, int64(pri), cId)
	if err != nil {
		return err
	}

	err = s.AddBusBalance(ctx, sId, int64(pri), cId)
	if err != nil {
		return err
	}

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

	

	updateTime := strconv.FormatInt(time.Now().Unix(), 10)
	year := strconv.FormatInt(int64(time.Now().Year()),10)
	month := time.Now().Format("1")
	trade := Trade{
		Id:             tId,
		TradeTime:      tradeTime,
		TradeDay:       strconv.FormatInt((int64(time.Now().Day())), 10),
		TradeMonth:     month,
		TradeYear:      year,
		Price:          int64(pri),
		BuyerId:        bId,
		SalerId:        sId,
		CommodityId:    cId,
		Valid:          true,
		LastUpdateTime: updateTime,
	}

	tradeAsBytes, _ := json.Marshal(trade)
	err = ctx.GetStub().PutState(trade.Id, tradeAsBytes)

	if err != nil {
		return fmt.Errorf(ERROR_CODE_PUTCHAINFAILED)
	}

	// 开始创建复合键

	err = s.createCompositeKeyandSave(ctx, INDEXNAME_BUYER_TRADE, []string{trade.BuyerId, trade.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, INDEXNAME_SALER_TRADE, []string{trade.SalerId, trade.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, INDEXNAME_BUYER_MONTH_YEAR_TRADE, []string{trade.BuyerId, trade.TradeMonth, trade.TradeYear,trade.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, INDEXNAME_BUYER_YEAR_TRADE, []string{trade.BuyerId, trade.TradeYear, trade.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, INDEXNAME_SALER_MONTH_YEAR_TRADE, []string{trade.SalerId, trade.TradeMonth, trade.TradeYear, trade.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, INDEXNAME_SALER_YEAR_TRADE, []string{trade.SalerId, trade.TradeYear, trade.Id})
	if err != nil {
		return err
	}

	// 修改commodity 状态
	err = s.ChangeCommodityBeBaught(ctx, cId, sId, bId, updateTime)
	if err != nil {
		return err
	}



	return nil
}

// 查询所有交易数据
func (s *SmartContract) QueryAllTrade(ctx contractapi.TransactionContextInterface)([]Trade,error){
	startKey := PREFIX_ID_TRADE + strings.Repeat("0", 25)
	endKey := PREFIX_ID_TRADE + strings.Repeat("9", 25)
	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)

	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	results := []Trade{}
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		trade := new(Trade)
		_ = json.Unmarshal(queryResponse.Value, trade)

		results = append(results, *trade)
		
	}
	return results, nil
}

// 统计某商家(by bussinessId/salerId)在某月(month)的盈利额
func (s *SmartContract) GetBusProfitByMonth(ctx contractapi.TransactionContextInterface, 
	busnissId string, month string, year string)(int, error){
	trades, err := s.QueryMonthTradeByBusiness(ctx, busnissId, month, year)

	if err != nil{
		return 0,err
	}

	profit := 0

	for _, trade := range trades{
		profit = profit + int(trade.Price)
	}
	return profit, nil

}

// 统计某用户(by buyerId/ customerId) 在某月(month)的消费额度
func(s *SmartContract) GetCusProfitByMonth(ctx contractapi.TransactionContextInterface,
customerId string, month string, year string)(int, error){
	trades, err := s.QueryMonthTradeByCus(ctx, customerId, month, year)

	if err != nil{
		return 0,nil
	}

	profit := 0

	for _,trade := range trades{
		profit = profit + int(trade.Price)
	}
	return profit, nil
}

// 查询某商家(by bussinessId/salerId)在某月(month)的交易数据
func (s *SmartContract) QueryMonthTradeByBusiness(ctx contractapi.TransactionContextInterface, 
	busnissId string, month string, year string)([]Trade, error){
	if !strings.HasPrefix(busnissId, PREFIX_ID_BUSSINIESSMAN){
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(INDEXNAME_SALER_MONTH_YEAR_TRADE, []string{busnissId, month, year})

	if err != nil{
		return nil, err
	}
	defer resultIterator.Close()

	result := []Trade{}

	for resultIterator.HasNext(){
		item, _ := resultIterator.Next()
		_, compositeKeyParts, err := ctx.GetStub().SplitCompositeKey(item.Key)
		if err != nil {
			return nil, err
		}
		id := compositeKeyParts[3]
		tradeAsBytes, err := ctx.GetStub().GetState(id)

		if err != nil{
			return nil, err
		}
		trade := new(Trade)
		_ = json.Unmarshal(tradeAsBytes, trade)
		result = append(result, *trade)

	}
	return result, nil
}

// 查询某买家(by buyerId/ customerId) 在某月(month)的交易数据

func (s *SmartContract) QueryMonthTradeByCus(ctx contractapi.TransactionContextInterface, 
cusId string, month string, year string)([]Trade, error){

	if !strings.HasPrefix(cusId, PREFIX_ID_CUSTOMER){
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(INDEXNAME_BUYER_MONTH_YEAR_TRADE, []string{cusId, month, year})

	if err != nil{
		return nil, err
	}
	defer resultIterator.Close()
	result := []Trade{}

	for resultIterator.HasNext(){
		item, _ := resultIterator.Next()
		_, compositeKeyParts, err := ctx.GetStub().SplitCompositeKey(item.Key)
		if err != nil {
			return nil, err
		}
		id := compositeKeyParts[3]
		tradeAsBytes, err := ctx.GetStub().GetState(id)

		if err != nil{
			return nil, err
		}
		trade := new(Trade)
		_ = json.Unmarshal(tradeAsBytes, trade)
		result = append(result, *trade)

	}
	return result, nil

}
