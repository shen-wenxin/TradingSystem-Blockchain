package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// t_bussiness_account
type Business struct {
	Id              string   `json:"id"`              // Id号，全局唯一
	Name            string   `json:"name"`            // 名字
	Phone           string   `json:"phone"`           // 商家联系方式
	CommodityIdList []string `json:"commodityIdList"` // 商品表
	CommodityCount  int64    `json:"commodityCount"`  // 已卖出商品数量统计
	Balance         int64    `json:"balance"`         // 余额
	Currency        string   `json:"currency"`        // 币种
	DiscountList    []string `json:"discountList"`    // 派发的优惠卷
	State           bool     `json:"state"`           // 账户状态
	LastUpdateTime  string   `json:"lastUpdateTime"`  // 最近更新时间
}

// QueryBusiness returns the businessman stored in the world state with given id
func (s *SmartContract) QueryBusiness(ctx contractapi.TransactionContextInterface, id string) (*Business, error) {

	if !strings.HasPrefix(id, PREFIX_ID_BUSSINIESSMAN) {
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	businessAsBytes, err := ctx.GetStub().GetState(id)

	if err != nil {
		return nil, fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}

	if businessAsBytes == nil {
		return nil, fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	bus := new(Business)
	_ = json.Unmarshal(businessAsBytes, bus)

	return bus, nil
}

// Creare Business add a new Business to the world state with given details
func (s *SmartContract) CreateBusiness(ctx contractapi.TransactionContextInterface, id string, name string, phone string) error {

	if !strings.HasPrefix(id, PREFIX_ID_BUSSINIESSMAN) {
		return fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	// 在create 之前 判断id 值是否已经被使用
	flag, err := s.idOnChainCheck(ctx, id)
	if err != nil {
		return fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}
	if flag {
		return fmt.Errorf(ERROR_CODE_EXISTINGDATA)
	}

	business := Business{
		Id:              id,
		Name:            name,
		Phone:           phone,
		CommodityIdList: []string{},
		CommodityCount:  0,
		Balance:         0,
		Currency:        CURRENCY_RMB,
		DiscountList:    []string{},
		State:           ACCOUNT_STATE_INVALID,
		LastUpdateTime:  strconv.FormatInt(time.Now().Unix(), 10),
	}

	businessAsBytes, _ := json.Marshal(business)
	return ctx.GetStub().PutState(business.Id, businessAsBytes)
}

// 返回所有的商家数据
func (s *SmartContract) QueryAllBussniss(ctx contractapi.TransactionContextInterface) ([]Business, error) {
	startKey := PREFIX_ID_BUSSINIESSMAN + strings.Repeat("0", 15)
	endKey := PREFIX_ID_BUSSINIESSMAN + strings.Repeat("9", 15)

	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)

	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	results := []Business{}

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		business := new(Business)
		_ = json.Unmarshal(queryResponse.Value, business)
		results = append(results, *business)
	}
	return results, nil

}

func (s *SmartContract) QueryBusBalance(ctx contractapi.TransactionContextInterface, bid string) (int64, error) {

	bus, err := s.QueryBusiness(ctx, bid)

	if err != nil {
		return 0, err
	}

	return bus.Balance, nil
}

// 增加商家的余额, 增加已售出数量, 在commodityList中去掉该商品
func (s *SmartContract) AddBusBalance(ctx contractapi.TransactionContextInterface, 
	bid string, price int64, commodityId string) error {

	bus, err := s.QueryBusiness(ctx, bid)

	if err != nil {
		return err
	}
	bus.Balance = bus.Balance + price

	bus.CommodityCount = bus.CommodityCount + 1
	bus.CommodityIdList = removeElemfromList(bus.CommodityIdList, commodityId)

	businessAsBytes, _ := json.Marshal(bus)
	return ctx.GetStub().PutState(bus.Id, businessAsBytes)
}

// 增加商家的商品list
func (s *SmartContract) addBusCommodityList(ctx contractapi.TransactionContextInterface,
	bid string, commodityId string) error {
	bus, err := s.QueryBusiness(ctx, bid)
	if err != nil {
		return err
	}
	bus.CommodityIdList = append(bus.CommodityIdList, commodityId)
	businessAsBytes, _ := json.Marshal(bus)
	return ctx.GetStub().PutState(bus.Id, businessAsBytes)
}

func removeElemfromList(oldList[]string, ele string)([]string){
	res := []string{}
	for _, str := range oldList{
		if str != ele{
			res = append(res, str)

		}
	}
	return res
}

