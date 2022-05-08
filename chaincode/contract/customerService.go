package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// t_customer_account
type Customer struct {
	Id              string   `json:"id"`              // Id号，全局唯一
	Name            string   `json:"name"`            //名字
	Phone           string   `json:"phone"`           // 消费者联系方式
	DiscountList    []string `json:"discountList"`    // 优惠卷id表
	CommodityIdList []string `json:"commodityIdList"` //拥有商品id 表
	Balance         int64    `json:"balance"`         // 余额
	Currency        string   `json:"currency"`        //币种
	State           bool     `json:"state"`           // 状态
	LastUpdateTime  string   `json:"lastUpdateTime"`  // 最近更新时间
}

//============customer============
// QueryAllSuperviser returns all Supervisers found in world state
func (s *SmartContract) QueryAllCustomer(ctx contractapi.TransactionContextInterface) ([]CustomerQueryResult, error) {
	startKey := PREFIX_ID_CUSTOMER + strings.Repeat("0", 11)
	endKey := PREFIX_ID_CUSTOMER + strings.Repeat("9", 11)

	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)

	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	results := []CustomerQueryResult{}

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		key := queryResponse.Key
		if strings.HasPrefix(key, PREFIX_ID_CUSTOMER) {
			if err != nil {
				return nil, err
			}
			customer := new(Customer)
			_ = json.Unmarshal(queryResponse.Value, customer)

			queryResult := CustomerQueryResult{Key: queryResponse.Key, Record: customer}
			results = append(results, queryResult)
		}
	}
	return results, nil
}

// Creare Superviser add a new superviser to the world state with given details
func (s *SmartContract) CreateCustomer(ctx contractapi.TransactionContextInterface, id string, name string, phone string) error {

	if !strings.HasPrefix(id, PREFIX_ID_CUSTOMER) {
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

	customer := Customer{
		Id:              id,
		Name:            name,
		Phone:           phone,
		DiscountList:    []string{},
		CommodityIdList: []string{},
		Balance:         0,
		Currency:        CURRENCY_RMB,
		State:           ACCOUNT_STATE_INVALID,
		LastUpdateTime:  strconv.FormatInt(time.Now().Unix(), 10),
	}

	customerAsBytes, _ := json.Marshal(customer)
	return ctx.GetStub().PutState(customer.Id, customerAsBytes)

}

// QueryCustomer returns the Customer stored in the world state with given id
func (s *SmartContract) QueryCustomer(ctx contractapi.TransactionContextInterface, id string) (*Customer, error) {

	if !strings.HasPrefix(id, PREFIX_ID_CUSTOMER) {
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	customerAsBytes, err := ctx.GetStub().GetState(id)

	if err != nil {
		return nil, fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}

	if customerAsBytes == nil {
		return nil, fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	customer := new(Customer)
	_ = json.Unmarshal(customerAsBytes, customer)

	return customer, nil
}

func (s *SmartContract) QueryCustomerName(ctx contractapi.TransactionContextInterface,cid string)(string, error){

	if !strings.HasPrefix(cid, PREFIX_ID_CUSTOMER){
		return "", fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	customerAsBytes, err := ctx.GetStub().GetState(cid)

	if err != nil {
		return "", fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}

	if customerAsBytes == nil {
		return "", fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	customer := new(Customer)
	_ = json.Unmarshal(customerAsBytes, customer)

	return customer.Name, nil
}

// DeleteCustomer delete the customer with the id given in world state
func (s *SmartContract) DeleteCustomer(ctx contractapi.TransactionContextInterface, id string) error {
	if !strings.HasPrefix(id, PREFIX_ID_CUSTOMER) {
		return fmt.Errorf(ERROR_CODE_ILLEGALID)
	}
	return ctx.GetStub().DelState(id)

}
