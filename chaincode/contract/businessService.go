package main

import (
	"encoding/json"
	"fmt"
	"strings"

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
