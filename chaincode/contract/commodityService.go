package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// t_commodity
type Commmodity struct {
	Id             string `json:"id"`         // 商品id
	Name           string `json:"name"`       // 商品名称
	Price          int64  `json:"price"`      //价格
	Currency       string `json:"currency"`   // 币种
	IssuerId       string `json:"issuerId"`   // 发行者id
	OwnerId        string `json:"ownerId"`    // 所属人id
	LastUpdateTime string `json:"lastUpdate"` // 最近更新时间
	State          string `json:"state"`      // 状态
}

const (
	STATE_ON_SALE     = "0000"
	STATE_OFF_SALE    = "0002"
	STATE_BE_BAUGHT   = "0003"
	STATE_BE_RETURNED = "0004"
)

// QueryResult stucture used for handling result of query bussinessman
func (s *SmartContract) CreateCommodity(ctx contractapi.TransactionContextInterface,
	id string, name string, price int, issuer string) error {
	if !strings.HasPrefix(id, PREFIX_ID_COMMODITY) {
		return fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	// 在create 之前, 判断id值是否已经被使用
	flag, err := s.idOnChainCheck(ctx, id)

	if err != nil {
		return fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}
	if flag {
		return fmt.Errorf(ERROR_CODE_EXISTINGDATA)
	}

	commodity := Commmodity{
		Id:             id,
		Name:           name,
		Price:          int64(price),
		Currency:       CURRENCY_RMB,
		IssuerId:       issuer,
		OwnerId:        "",
		LastUpdateTime: strconv.FormatInt(time.Now().Unix(), 10),
		State:          STATE_ON_SALE,
	}

	commodityAsBytes, _ := json.Marshal(commodity)

	err = ctx.GetStub().PutState(commodity.Id, commodityAsBytes)

	if err != nil {
		return fmt.Errorf(ERROR_CODE_PUTCHAINFAILED)
	}

	// 开始创建复合键
	err = s.createCompositeKeyandSave(ctx, "name~id", []string{commodity.Name, commodity.Id})
	if err != nil {
		return err
	}
	return s.createCompositeKeyandSave(ctx, "Issuer~id", []string{commodity.IssuerId, commodity.Id})
}

func (s *SmartContract) QueryCommodityByIssuer(ctx contractapi.TransactionContextInterface, issuer string) ([]Commmodity, error) {
	indexName := "Issuer~id"
	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(indexName, []string{issuer})
	if err != nil {
		return nil, err
	}
	defer resultIterator.Close()
	result := []Commmodity{}
	for resultIterator.HasNext() {
		item, _ := resultIterator.Next()
		_, compositeKeyParts, err := ctx.GetStub().SplitCompositeKey(item.Key)
		if err != nil {
			return nil, err
		}
		id := compositeKeyParts[1]

		commodityAsBytes, err := ctx.GetStub().GetState(id)

		if err != nil {
			return nil, err
		}
		commodity := new(Commmodity)

		_ = json.Unmarshal(commodityAsBytes, commodity)

		result = append(result, *commodity)
	}

	return result, nil
}

// 通过id号查询商品号
func (s *SmartContract) QueryCommodityById(ctx contractapi.TransactionContextInterface, id string) (*Commmodity, error) {
	if !strings.HasPrefix(id, PREFIX_ID_COMMODITY) {
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	commodityAsBytes, err := ctx.GetStub().GetState(id)

	if err != nil {
		return nil, fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}

	if commodityAsBytes == nil {
		return nil, fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	commodity := new(Commmodity)

	_ = json.Unmarshal(commodityAsBytes, commodity)

	return commodity, nil
}

func (s *SmartContract) QueryAllCommodity(ctx contractapi.TransactionContextInterface, id string) ([]Commmodity, error) {
	startKey := PREFIX_ID_COMMODITY + strings.Repeat("0", 11)
	endKey := PREFIX_ID_COMMODITY + strings.Repeat("9", 11)

	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)

	if err != nil {
		return nil, err
	}

	defer resultsIterator.Close()

	results := []Commmodity{}

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()

		if err != nil {
			return nil, err
		}

		commodity := new(Commmodity)
		_ = json.Unmarshal(queryResponse.Value, commodity)

		results = append(results, *commodity)
	}

	return results, nil

}
