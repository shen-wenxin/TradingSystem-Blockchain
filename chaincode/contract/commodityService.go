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
	IssuerName     string `json:"issuerName"` // 发行者名字
	OwnerId        string `json:"ownerId"`    // 所属人id
	OwnerName      string `json:"ownerName"`  // 所属人名字
	LastUpdateTime string `json:"lastUpdate"` // 最近更新时间
	State          string `json:"state"`      // 状态
}

const (
	STATE_ON_SALE     = "0000"
	STATE_OFF_SALE    = "0001"
	STATE_BE_BAUGHT   = "0002"
	STATE_BE_RETURNED = "0003"
)

// QueryResult stucture used for handling result of query bussinessman
func (s *SmartContract) CreateCommodity(ctx contractapi.TransactionContextInterface,
	id string, name string, price string, issuer string, issuerName string) error {
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

	pri, err := strconv.Atoi(price)

	commodity := Commmodity{
		Id:             id,
		Name:           name,
		Price:          int64(pri),
		Currency:       CURRENCY_RMB,
		IssuerId:       issuer,
		IssuerName:     issuerName,
		OwnerId:        issuer,
		OwnerName:      issuerName,
		LastUpdateTime: strconv.FormatInt(time.Now().Unix(), 10),
		State:          STATE_ON_SALE,
	}

	commodityAsBytes, _ := json.Marshal(commodity)

	err = ctx.GetStub().PutState(commodity.Id, commodityAsBytes)

	if err != nil {
		return fmt.Errorf(ERROR_CODE_PUTCHAINFAILED)
	}

	// 开始修改bussniss状态 将commidty 加入其commodityList
	

	// 开始创建复合键
	// 名字~id号复合键
	err = s.createCompositeKeyandSave(ctx, "name~id", []string{commodity.Name, commodity.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, "state~issuer~id", []string{commodity.State, commodity.IssuerId, commodity.Id})
	if err != nil {
		return err
	}

	err = s.createCompositeKeyandSave(ctx, "state~id", []string{commodity.State, commodity.Id})
	if err != nil {
		return err
	}

	return s.createCompositeKeyandSave(ctx, "Issuer~id", []string{commodity.IssuerId, commodity.Id})
}

func (s *SmartContract) ChangeCommodityBeBaught(ctx contractapi.TransactionContextInterface,
	cid string, sid string, bid string, tTime string) error {
	// cid commodityId	sid salerId(businessId) bid buyerId(customerId) tTime(tradeTime)
	commodityAsBytes, err := ctx.GetStub().GetState(cid)

	if err != nil {
		return err
	}

	if commodityAsBytes == nil {
		return fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	commodity := new(Commmodity)

	_ = json.Unmarshal(commodityAsBytes, commodity)

	// 删除相关复合键
	err = s.deleteCompositeKey(ctx, "state~issuer~id", []string{commodity.State, commodity.IssuerId, commodity.Id})
	if err != nil {
		return err
	}

	err = s.deleteCompositeKey(ctx, "state~id", []string{commodity.State, commodity.Id})
	if err != nil {
		return err
	}

	// 修改commodity 状态
	ownerName, err := s.QueryCustomerName(ctx, bid)

	if commodityAsBytes == nil {
		return fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	commodity.OwnerId = bid
	commodity.OwnerName = ownerName
	commodity.State = STATE_BE_BAUGHT
	commodity.LastUpdateTime = tTime

	// 将新数据存入

	newCommodityAsBytes, _ := json.Marshal(commodity)

	err = ctx.GetStub().PutState(commodity.Id, newCommodityAsBytes)
	if err != nil {
		return fmt.Errorf(ERROR_CODE_PUTCHAINFAILED)
	}

	// 创建新的复合键
	err = s.createCompositeKeyandSave(ctx, "state~issuer~id", []string{commodity.State, commodity.IssuerId, commodity.Id})
	if err != nil {
		return err
	}
	err = s.createCompositeKeyandSave(ctx, "state~id", []string{commodity.State, commodity.Id})
	if err != nil {
		return err
	}
	err = s.createCompositeKeyandSave(ctx, "state~owner~id", []string{commodity.State, commodity.OwnerId, commodity.Id})
	if err != nil {
		return err
	}

	return nil

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

// 获得正在售卖的商品(通过issuer)
func (s *SmartContract) QueryCommodityOnSaleByIssuer(ctx contractapi.TransactionContextInterface, issuer string) ([]Commmodity, error) {
	indexName := "state~issuer~id"
	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(indexName, []string{STATE_ON_SALE, issuer})
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
		id := compositeKeyParts[2]
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

// 获得已经出售的商品(通过issuer)
func (s *SmartContract) QueryCommoditySaledByIssuer(ctx contractapi.TransactionContextInterface, issuer string) ([]Commmodity, error) {
	indexName := "state~issuer~id"
	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(indexName, []string{STATE_BE_BAUGHT, issuer})
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
		id := compositeKeyParts[2]
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

// 返回所有待售商品
func (s *SmartContract) QueryCommodityOnSale(ctx contractapi.TransactionContextInterface) ([]Commmodity, error) {
	indexName := "state~id"
	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(indexName, []string{STATE_ON_SALE})
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

// 返回所有已被 customerId 购买的商品
func (s *SmartContract) QueryAllCommoditybeBaughtByCustomer(ctx contractapi.TransactionContextInterface, cId string) ([]Commmodity, error) {
	if !strings.HasPrefix(cId, PREFIX_ID_CUSTOMER) {
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	indexName := "state~owner~id"

	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(indexName, []string{STATE_BE_BAUGHT, cId})
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
		id := compositeKeyParts[2]
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

// 得到所有商品
func (s *SmartContract) QueryAllCommodity(ctx contractapi.TransactionContextInterface) ([]Commmodity, error) {
	startKey := PREFIX_ID_COMMODITY + strings.Repeat("0", 20)
	endKey := PREFIX_ID_COMMODITY + strings.Repeat("9", 20)

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
