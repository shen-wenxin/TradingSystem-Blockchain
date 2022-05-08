package main

import (
	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// idOnChainCheck check if the id has been used as the key on blockchain
func (s *SmartContract) DataOnChainCheck(ctx contractapi.TransactionContextInterface, id string) (bool, error) {
	dataAsBytes, _ := ctx.GetStub().GetState(id)

	if dataAsBytes == nil {
		res := false
		return res, nil
	}
	return true, nil
}

// 创建复合键并存储
func (s *SmartContract) createCompositeKeyandSave(ctx contractapi.TransactionContextInterface,
	indexName string, attributes []string) error {
	indexKey, err := ctx.GetStub().CreateCompositeKey(indexName, attributes)
	if err != nil {
		return err
	}
	value := []byte{0x00}
	return ctx.GetStub().PutState(indexKey, value)
}

// 删除链上复合键
func (s *SmartContract) deleteCompositeKey(ctx contractapi.TransactionContextInterface,
	indexName string, attributes []string) error {
	indexKey, err := ctx.GetStub().CreateCompositeKey(indexName, attributes)
	if err != nil{
		return err
	}
	return ctx.GetStub().DelState(indexKey)
}