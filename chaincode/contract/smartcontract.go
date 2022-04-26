/*
SPDX-License-Identifier: Apache-2.0
*/

package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// SmartContract provides functions for managing a car
type SmartContract struct {
	contractapi.Contract
}

// data structure
type Superviser struct {
	Id             string `json:"id"`             //id号，全局唯一
	Name           string `json:"name"`           //名字
	State          bool   `json:"state"`          // 账号状态
	Remakes        string `json:"remakes"`        // 备注
	LastUpdateTime string `json:"lastUpdateTime"` //最近更新时间
}

// t_customer_account
type Customer struct {
	AccountId       string   `json:"accountId"`       // Id号，全局唯一
	DiscountList    []string `json:"discountList"`    // 优惠卷id表
	CommodityIdList []string `json:"commodityIdList"` //拥有商品id 表
	Balance         int64    `json:"balance"`         // 余额
	Currency        string   `json:"currency"`        //币种
	State           bool     `json:"state"`           // 状态
	LastUpdateTime  string   `json:"lastUpdateTime"`  // 最近更新时间
}

// t_bussiness_account
type Bussiness struct {
	AccountId       string   `json:"accountId"`       // Id号，全局唯一
	CommodityIdList []string `json:"commodityIdList"` // 商品表
	Balance         int64    `json:"balance"`         // 余额
	Currency        string   `json:"currency"`        // 币种
	DiscountList    []string `json:"discountList"`    // 派发的优惠卷
	State           bool     `json:"state"`           // 账户状态
	LastUpdateTime  string   `json:"lastUpdateTime"`  // 最近更新时间
}

// t_commodity
type Commmodity struct {
	CommodityId string `json:"commodityId"` // 商品id
	Name        string `json:"name"`        // 商品名称
	Price       int64  `json:"price"`       //价格
	Currency    string `json:"currency"`    // 币种
	IssuerId    string `json:"issuerId"`    // 发行者id
	OwnerId     string `json:"ownerId"`     // 所属人id
	LastUpdate  string `json:"lastUpdate"`  // 最近更新时间
	State       string `json:"state"`       // 状态
	Remakes     string `json:"remakes"`     // 备注
}

// t_trade
type Trade struct {
	TradeId        string `json:"tradeId"`        // 交易id号
	TradeTime      string `json:"tradeTime"`      // 交易时间
	Price          string `json:"price"`          // 价格
	BuyerId        string `json:"buyerId"`        // 消费者id
	SalerId        string `json:"salerId"`        //商家id
	CommodityId    string `json:"commodityId"`    // 商品id
	Valid          bool   `json:"valid"`          // 交易是否有效
	LastUpdateTime string `json:"lastUpdateTime"` // 最近更新时间
	Remakes        string `json:"remakes"`        // 备注
}

// t_operate
type Operate struct {
	Id            string `json:"tradeId"`       // 操作id
	Time          string `json:"time"`          // 时间
	OperaterId    string `json:"operaterId"`    // 操作员id
	OperatereedId string `json:"operatereedId"` // 被操作者id
	OperateType   string `json:"operateType"`   // 操作类型
}

// t_discount
type Discount struct {
	DiscountId string `json:"discountId"` // id,全局唯一
	Name       string `json:"name"`       // 姓名
	AccountId  string `json:"accountId"`  // 所属者id
	BusinessId string `json:"businessId"` // 派发商户id
	BeginTime  string `json:"beginTime"`  // 开始时间
	EndTime    string `json:"endTime"`    // 结束时间
	Valid      bool   `json:"valid"`      // 有效
}

// 常量定义
// 币种
const (
	CURRENCY_RMB = "0001" // 人民币
)

// 商品状态码
const (
	COMMMODITY_STATE_ONSALE   = "0001" // 商品待售
	COMMMODITY_STATE_OFFSATE  = "0002" // 商品下架
	COMMMODITY_STATE_BEBOUGHT = "0003" // 已被购买
	COMMMODITY_STATE_RETURNED = "0004" // 商品已被退回
)

// remarks for t_trade
const (
	REMAKES_TRADE_BOUGHT = "Be bought"
	REMAKES_TRADE_RETURN = "returned"
)

// id prefix
const (
	PREFIX_ID_CUSTOMER      = "0000"
	PREFIX_ID_BUSSINIESSMAN = "0001"
	PREFIX_ID_COMMIDITY     = "0002"
	PREFIX_ID_SUPERVISER    = "0003"
)

// account state
const (
	ACCOUNT_STATE_VALID   = true
	ACCOUNT_STATE_INVALID = false
)

// 错误码定义
const (
	ERROR_CODE_MARSHAL        = "E0001"
	ERROR_CODE_ILLEGALID      = "E0002" // 数据不合法
	ERROR_CODE_GETCHAINFAILED = "E0003" // 查询链上数据失败
	ERROR_CODE_EXISTINGDATA   = "E0004" // 链上已存在数据
	ERROR_CODE_PUTCHAINFAILED = "E0005"
	ERROR_CODE_USERINVALID    = "E0006" //用户不合法
	ERROR_CODE_UNEXISTDATA    = "E0007" // 链上不存在数据
)

// QueryResult structure used for handling result of query superviser
type SuperviserQueryResult struct {
	Key    string `json:"Key"`
	Record *Superviser
}

// InitLedger adds a base set of cars to the ledger
func (s *SmartContract) InitLedger(ctx contractapi.TransactionContextInterface) error {
	// 定义最初始的管理员身份
	supervisers := []Superviser{
		{Id: PREFIX_ID_SUPERVISER + strings.Repeat("0", 11), Name: "admin", State: ACCOUNT_STATE_VALID, Remakes: "init create", LastUpdateTime: strconv.FormatInt(time.Now().Unix(), 10)},
	}

	for _, superviser := range supervisers {
		superviserAsBytes, _ := json.Marshal(superviser)
		err := ctx.GetStub().PutState(superviser.Id, superviserAsBytes)
		if err != nil {
			return fmt.Errorf("Failed to put to world state. %s", err.Error())
		}

	}
	return nil
}

// idOnChainCheck check if the id has been used as the key on blockchain
func (s *SmartContract) idOnChainCheck(ctx contractapi.TransactionContextInterface, id string) (bool, error) {

	superviserAsBytes, err := ctx.GetStub().GetState(id)

	if err != nil {
		return true, fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}

	if superviserAsBytes == nil {
		return false, nil
	}

	return true, nil
}

// Superviser
// QueryAllSuperviser returns all cars found in world state
func (s *SmartContract) QueryAllSuperviser(ctx contractapi.TransactionContextInterface) ([]SuperviserQueryResult, error) {
	startKey := PREFIX_ID_SUPERVISER + strings.Repeat("0", 11)
	endKey := PREFIX_ID_SUPERVISER + strings.Repeat("9", 11)

	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)

	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	results := []SuperviserQueryResult{}

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		key := queryResponse.Key
		if strings.HasPrefix(key, PREFIX_ID_SUPERVISER) {
			if err != nil {
				return nil, err
			}
			superviser := new(Superviser)
			_ = json.Unmarshal(queryResponse.Value, superviser)

			queryResult := SuperviserQueryResult{Key: queryResponse.Key, Record: superviser}
			results = append(results, queryResult)
		}
	}
	return results, nil
}

// Creare Superviser add a new superviser to the world state with given details
func (s *SmartContract) CreateSuperviser(ctx contractapi.TransactionContextInterface, id string, name string, remarks string) error {

	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
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

	superviser := Superviser{
		Id:             id,
		Name:           name,
		State:          ACCOUNT_STATE_VALID,
		Remakes:        remarks,
		LastUpdateTime: strconv.FormatInt(time.Now().Unix(), 10),
	}

	superviserAsBytes, _ := json.Marshal(superviser)
	return ctx.GetStub().PutState(superviser.Id, superviserAsBytes)

}

// QuerySuperviser returns the superviser stored in the world state with given id
func (s *SmartContract) QuerySuperviser(ctx contractapi.TransactionContextInterface, id string) (*Superviser, error) {

	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	superviserAsBytes, err := ctx.GetStub().GetState(id)

	if err != nil {
		return nil, fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}

	if superviserAsBytes == nil {
		return nil, fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	superviser := new(Superviser)
	_ = json.Unmarshal(superviserAsBytes, superviser)

	return superviser, nil
}

// ChangeSuperviserName updates the name field of superviser with given id in the world state
func (s *SmartContract) ChangeSuperviserName(ctx contractapi.TransactionContextInterface, id string, newname string) error {

	superviser, err := s.QuerySuperviser(ctx, id)

	if err != nil {
		return err
	}
	if superviser.State == ACCOUNT_STATE_INVALID {
		return fmt.Errorf(ERROR_CODE_USERINVALID)
	}

	superviser.Name = newname
	superviser.LastUpdateTime = strconv.FormatInt(time.Now().Unix(), 10)

	superviserAsBytes, _ := json.Marshal(superviser)

	return ctx.GetStub().PutState(superviser.Id, superviserAsBytes)
}

// DeleteSuperviser delete the superviser with the id given in world state
func (s *SmartContract) DeleteSuperviser(ctx contractapi.TransactionContextInterface, id string) error {
	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
		return fmt.Errorf(ERROR_CODE_ILLEGALID)
	}
	return ctx.GetStub().DelState(id)
}

func main() {

	chaincode, err := contractapi.NewChaincode(new(SmartContract))

	if err != nil {
		fmt.Printf("Error create fabcar chaincode: %s", err.Error())
		return
	}

	if err := chaincode.Start(); err != nil {
		fmt.Printf("Error starting fabcar chaincode: %s", err.Error())
	}
}
