package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// data saved on chain, please see the database design interface for more details

// t_superviser
type Superviser struct {
	Id             string `json:"id"`
	Name           string `json:"name"`
	State          bool   `json:"state"`
	Remakes        string `json:"remakes"`
	LastUpdateTime string `json:"lastUpdateTime"`
}

// t_customer_account
type Customer struct {
	AccountId       string   `json:"accountId"`
	DiscountList    []string `json:"discountList"`
	CommodityIdList []string `json:"commodityIdList"`
	Balance         int64    `json:"balance"`
	Currency        string   `json:"currency"`
	State           bool     `json:"state"`
	LastUpdateTime  string   `json:"lastUpdateTime"`
}

// t_bussiness_account
type Bussiness struct {
	AccountId       string   `json:"accountId"`
	CommodityIdList []string `json:"commodityIdList"`
	Balance         int64    `json:"balance"`
	Currency        string   `json:"currency"`
	DiscountList    []string `json:"discountList"`
	State           bool     `json:"state"`
	LastUpdateTime  string   `json:"lastUpdateTime"`
}

// t_commodity
type Commmodity struct {
	CommodityId string `json:"commodityId"`
	Name        string `json:"name"`
	Price       string `json:"price"`
	Currency    string `json:"currency"`
	IssuerId    string `json:"issuerId"`
	OwnerId     string `json:"ownerId"`
	LastUpdate  string `json:"lastUpdate"`
	State       string `json:"state"`
	Remakes     string `json:"remakes"`
}

// t_trade
type Trade struct {
	TradeId        string `json:"tradeId"`
	TradeTime      string `json:"tradeTime"`
	BuyerId        string `json:"buyerId"`
	SalerId        string `json:"salerId"`
	CommodityId    string `json:"commodityId"`
	Valid          bool   `json:"valid"`
	LastUpdateTime string `json:"lastUpdateTime"`
	Remakes        string `json:"remakes"`
}

// t_operate
type Operate struct {
	Id            string `json:"tradeId"`
	Time          string `json:"time"`
	OperaterId    string `json:"operaterId"`
	OperatereedId string `json:"operatereedId"`
	OperateType   string `json:"operateType"`
}

// t_discount
type Discount struct {
	DiscountId string `json:"discountId"`
	Name       string `json:"name"`
	AccountId  string `json:"accountId"`
	BusinessId string `json:"businessId"`
	BeginTime  string `json:"beginTime"`
	EndTime    string `json:"endTime"`
	Valid      bool   `json:"valid"`
}

// FORMAT please see the Constant Definitions - Prefix Definitions for more details
// currency code
const (
	CURRENCY_RMB = "0001"
)

// commodity state code
const (
	COMMMODITY_STATE_ONSALE   = "0001"
	COMMMODITY_STATE_OFFSATE  = "0002"
	COMMMODITY_STATE_BEBOUGHT = "0003"
	COMMMODITY_STATE_RETURNED = "0004"
)

// account state code
const (
	ACCOUNT_STATE_REGISTER = "0000"
	ACCOUNT_STATE_ACTIVE   = "0001"
	ACCOUNT_STATE_CANCEL   = "0002"
	ACCOUNT_STATE_DELETE   = "0003"
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

// error code.For the specific meaning of the error code,
//see Constant Definition -  Error Code Definition
const (
	ERROR_CODE_MARSHAL        = "E0001"
	ERROR_CODE_ILLEGALID      = "E0002"
	ERROR_CODE_GETCHAINFAILED = "E0003"
	ERROR_CODE_EXISTINGDATA   = "E0004"
	ERROR_CODE_PUTCHAINFAILED = "E0005"
)

// SimpleContract contract for handling writing and reading from the world state
type SimpleContract struct {
	contractapi.Contract
}

// id uniqueness check. Check if there is datas stored with this id on the chain
// if there is datas stored with this id, return false
// the function is not visible outside
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

// SuperviserRegistration defines functions which used for new superviser registration.
func (sc *SimpleContract) SuperviserRegistration(ctx contractapi.TransactionContextInterface,
	id string, name string, remarks string) error {

	//id prefix check
	//According to the regulations, all customer ids must start with PREFIX_ID_SUPERVISER,
	//if not, it is illegal id
	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
		return errors.New(ERROR_CODE_ILLEGALID)
	}

	//id uniqueness check
	err, valid := sc.idUniquenessCheck(ctx, id)
	if !valid {
		return err
	}

	sup := Superviser{
		Id:             id,
		Name:           name,
		State:          true,
		Remakes:        remarks,
		LastUpdateTime: string(rune(time.Now().Unix())),
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

// CustormerRegistration defines functions which used for new customer registration.
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
		LastUpdateTime:  string(rune(time.Now().Unix())),
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

// BussinessRegistration defines functions which used for new customer registration.
// The design of Bussiness and Customer data storage structure is very similar, and the registration method is
// very similar too. However, it is still disassembled into two methods to facilitate adding functions later.
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
		LastUpdateTime:  string(rune(time.Now().Unix())),
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
