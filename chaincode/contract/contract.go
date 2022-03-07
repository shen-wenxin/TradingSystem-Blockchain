package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"strings"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// data saved on chain, please see the database design interface for more details
// t_customer_account
type Customer struct {
	AccountId       string   `json:"accountId"`
	DiscountList    []string `json:"discountList"`
	CommodityIdList []string `json:"commodityIdList"`
	Balance         string   `json:"balance"`
	Currency        string   `json:"currency"`
	LastUpdateTime  string   `json:"lastUpdateTime"`
}

// t_bussiness_account
type Bussiness struct {
	AccountId       string   `json:"accountId"`
	CommodityIdList []string `json:"commodityIdList"`
	Balance         []string `json:"balance"`
	Currency        string   `json:"currency"`
	DiscountList    string   `json:"discountList"`
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

// new customer registration
func (sc *SimpleContract) CustormerRegistration(ctx contractapi.TransactionContextInterface, customer string) error {
	var cus Customer

	err := json.Unmarshal([]byte(customer), &cus)
	if err != nil {
		return errors.New(ERROR_CODE_MARSHAL)
	}

	//id prefix check
	//According to the regulations, all customer ids must start with PREFIX_ID_CUSTOMER,
	//if not, it is illegal id
	if !strings.HasPrefix(cus.AccountId, PREFIX_ID_CUSTOMER) {
		return errors.New(ERROR_CODE_ILLEGALID)
	}

	//id uniqueness check
	err, valid := sc.idUniquenessCheck(ctx, cus.AccountId)
	if !valid {
		return err
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
