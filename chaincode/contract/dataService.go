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