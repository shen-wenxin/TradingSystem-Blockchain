package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// 链上数据结构
// t_superviser 监管人员
type Superviser struct {
	Id             string `json:"id"`             //id号，全局唯一
	Name           string `json:"name"`           //名字
	State          bool   `json:"state"`          // 账号状态
	Remakes        string `json:"remakes"`        // 备注
	LastUpdateTime string `json:"lastUpdateTime"` //最近更新时间
}

// t_customer_account	消费者信息
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
	PREFIX_ID_SUPERVISER    = "0004"
)

// account state
const (
	ACCOUNT_STATE_VALID   = true
	ACCOUNT_STATE_INVALID = false
)

// 错误码定义
const (
	ERROR_CODE_MARSHAL        = "E0001"
	ERROR_CODE_ILLEGALID      = "E0002"
	ERROR_CODE_GETCHAINFAILED = "E0003"
	ERROR_CODE_EXISTINGDATA   = "E0004"
	ERROR_CODE_PUTCHAINFAILED = "E0005"
	ERROR_CODE_USERINVALID    = "E0006" //用户不合法
	ERROR_CODE_UNEXISTDATA    = "E0007" // 链上不存在数据
)

// SimpleContract contract for handling writing and reading from the world state
type SimpleContract struct {
	contractapi.Contract
}

// id 唯一性检查，检查链上是否有该id为key值存储的数据，如果有，返回false(不唯一)
// 该 function 外部不可见
func (sc *SimpleContract) idUniquenessCheck(ctx contractapi.TransactionContextInterface, id string) (error, bool) {
	existing, err := ctx.GetStub().GetState(id)
	if err != nil {
		return errors.New(ERROR_CODE_GETCHAINFAILED), false
	}
	if existing != nil {
		return errors.New(ERROR_CODE_EXISTINGDATA), false
	}
	return nil, true
}

// BussinessIdentityCheck 用来检查 ID 为id 的商户是否为合法用户
func (sc *SimpleContract) BussinessIdentityCheck(ctx contractapi.TransactionContextInterface, id string) (error, bool) {

	if !strings.HasPrefix(id, PREFIX_ID_BUSSINIESSMAN) {
		return errors.New(ERROR_CODE_ILLEGALID), false
	}

	// 检查链上是否有为该id的商户的数据
	existing, err := ctx.GetStub().GetState(id)
	if err != nil {
		return errors.New(ERROR_CODE_GETCHAINFAILED), false
	}
	if existing == nil {
		return errors.New(ERROR_CODE_UNEXISTDATA), false
	}

	var bus Bussiness
	err = json.Unmarshal(existing, &bus)

	if err != nil {
		return errors.New(ERROR_CODE_MARSHAL), false
	}

	// 数据合法性检查
	if bus.State == ACCOUNT_STATE_INVALID {
		// 商家状态不合法
		return errors.New(ERROR_CODE_USERINVALID), false
	}

	return nil, true
}

// SuperviserRegistration 监管人员身份注册
func (sc *SimpleContract) SuperviserRegistration(ctx contractapi.TransactionContextInterface,
	id string, name string, remarks string) error {

	// id 前缀检查
	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
		return errors.New(ERROR_CODE_ILLEGALID)
	}

	//id 唯一性检查
	err, valid := sc.idUniquenessCheck(ctx, id)
	if !valid {
		return err
	}

	sup := Superviser{
		Id:             id,
		Name:           name,
		State:          ACCOUNT_STATE_VALID,
		Remakes:        remarks,
		LastUpdateTime: strconv.FormatInt(time.Now().Unix(), 10),
	}

	// sup struct to string
	supjson, err := json.Marshal(sup)
	if err != nil {
		return errors.New(ERROR_CODE_MARSHAL)
	}

	err = ctx.GetStub().PutState(sup.Id, supjson)
	if err != nil {
		return errors.New(ERROR_CODE_PUTCHAINFAILED)
	}
	return nil
}

// CustormerRegistration 消费者身份注册.
func (sc *SimpleContract) CustormerRegistration(ctx contractapi.TransactionContextInterface,
	id string) error {

	if !strings.HasPrefix(id, PREFIX_ID_CUSTOMER) {
		return errors.New(ERROR_CODE_ILLEGALID)
	}
	//id uniqueness check
	err, valid := sc.idUniquenessCheck(ctx, id)
	if !valid {
		return err
	}

	discount := []string{}
	commmodity := []string{}

	cus := Customer{
		AccountId:       id,
		DiscountList:    discount,
		CommodityIdList: commmodity,
		Balance:         0,
		Currency:        CURRENCY_RMB,
		State:           ACCOUNT_STATE_VALID,
		LastUpdateTime:  strconv.FormatInt(time.Now().Unix(), 10),
	}
	// cus struct to string
	cusjson, err := json.Marshal(cus)
	if err != nil {
		return errors.New(ERROR_CODE_MARSHAL)
	}

	err = ctx.GetStub().PutState(cus.AccountId, []byte(cusjson))
	if err != nil {
		return errors.New(ERROR_CODE_PUTCHAINFAILED)
	}
	return nil
}

// BussinessRegistration  商家身份注册
func (sc *SimpleContract) BussinessRegistration(ctx contractapi.TransactionContextInterface, id string) error {

	if !strings.HasPrefix(id, PREFIX_ID_BUSSINIESSMAN) {
		return errors.New(ERROR_CODE_ILLEGALID)
	}

	//id uniqueness check
	err, valid := sc.idUniquenessCheck(ctx, id)
	if !valid {
		return err
	}

	discount := []string{}
	commmodity := []string{}

	bus := Bussiness{
		AccountId:       id,
		CommodityIdList: commmodity,
		DiscountList:    discount,
		Balance:         0,
		Currency:        CURRENCY_RMB,
		State:           ACCOUNT_STATE_VALID,
		LastUpdateTime:  strconv.FormatInt(time.Now().Unix(), 10),
	}

	// bus struct to string
	busjson, err := json.Marshal(bus)
	if err != nil {
		return errors.New(ERROR_CODE_MARSHAL)
	}

	err = ctx.GetStub().PutState(bus.AccountId, []byte(busjson))

	if err != nil {
		return errors.New(ERROR_CODE_PUTCHAINFAILED)
	}
	return nil
}

// CommodityOnSale  商品待售操作
// cid: commodityId
func (sc *SimpleContract) CommodityOnSale(ctx contractapi.TransactionContextInterface,
	cid string, name string, price int64, currency string, issuerid string, remarks string) error {

	// 商家身份检查
	err, valid := sc.BussinessIdentityCheck(ctx, issuerid)
	if !valid {
		return err
	}
	if !strings.HasPrefix(cid, PREFIX_ID_COMMIDITY) {
		return errors.New(ERROR_CODE_ILLEGALID)
	}

	err, valid = sc.idUniquenessCheck(ctx, cid)
	if valid {
		// TODO: 链上没有该商品的数据

	} else {
		// TODO:链上有该商品的数据

	}
	return nil

}

// Create adds a new key with value to the world state
func (sc *SimpleContract) Create(ctx contractapi.TransactionContextInterface, key string, value string) error {
	existing, err := ctx.GetStub().GetState(key)

	if err != nil {
		return errors.New("Unable to interact with world state")
	}

	if existing != nil {
		return fmt.Errorf("Cannot create world state pair with key %s. Already exists", key)
	}

	err = ctx.GetStub().PutState(key, []byte(value))

	if err != nil {
		return errors.New("Unable to interact with world state")
	}

	return nil
}

// Update changes the value with key in the world state
func (sc *SimpleContract) Update(ctx contractapi.TransactionContextInterface, key string, value string) error {
	existing, err := ctx.GetStub().GetState(key)

	if err != nil {
		return errors.New("Unable to interact with world state")
	}

	if existing == nil {
		return fmt.Errorf("Cannot update world state pair with key %s. Does not exist", key)
	}

	err = ctx.GetStub().PutState(key, []byte(value))

	if err != nil {
		return errors.New("Unable to interact with world state")
	}

	return nil
}

// Read returns the value at key in the world state
func (sc *SimpleContract) Read(ctx contractapi.TransactionContextInterface, key string) (string, error) {
	existing, err := ctx.GetStub().GetState(key)

	if err != nil {
		return "", errors.New("Unable to interact with world state")
	}

	if existing == nil {
		return "", fmt.Errorf("Cannot read world state pair with key %s. Does not exist", key)
	}

	return string(existing), nil
}
