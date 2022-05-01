package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

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
		AccountId:       id,
		Name:            name,
		Phone:           phone,
		CommodityIdList: []string{},
		Balance:         0,
		Currency:        CURRENCY_RMB,
		DiscountList:    []string{},
		State:           ACCOUNT_STATE_INVALID,
		LastUpdateTime:  strconv.FormatInt(time.Now().Unix(), 10),
	}

	businessAsBytes, _ := json.Marshal(business)
	return ctx.GetStub().PutState(business.AccountId, businessAsBytes)
}
