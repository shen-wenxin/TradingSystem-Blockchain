package main

import (
	"encoding/json"
	"fmt"
	"strconv"
	"strings"
	"time"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// ============Superviser============
// QueryAllSuperviser returns all Supervisers found in world state
func (s *SmartContract) QueryAllSuperviser(ctx contractapi.TransactionContextInterface) ([]SuperviserQueryResult, error) {
	startKey := PREFIX_ID_SUPERVISER + strings.Repeat("0", 11)
	endKey := PREFIX_ID_SUPERVISER + strings.Repeat("9", 11)

	resultsIterator, err := ctx.GetStub().GetStateByRange(startKey, endKey)

	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	results := []SuperviserQueryResult{}

	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		key := queryResponse.Key
		if strings.HasPrefix(key, PREFIX_ID_SUPERVISER) {
			if err != nil {
				return nil, err
			}
			superviser := new(Superviser)
			_ = json.Unmarshal(queryResponse.Value, superviser)

			queryResult := SuperviserQueryResult{Key: queryResponse.Key, Record: superviser}
			results = append(results, queryResult)
		}
	}
	return results, nil
}

// Creare Superviser add a new superviser to the world state with given details
func (s *SmartContract) CreateSuperviser(ctx contractapi.TransactionContextInterface, id string, name string, remarks string) error {

	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
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

	superviser := Superviser{
		Id:             id,
		Name:           name,
		State:          ACCOUNT_STATE_VALID,
		Remakes:        remarks,
		LastUpdateTime: strconv.FormatInt(time.Now().Unix(), 10),
	}

	superviserAsBytes, _ := json.Marshal(superviser)
	return ctx.GetStub().PutState(superviser.Id, superviserAsBytes)

}

// QuerySuperviser returns the superviser stored in the world state with given id
func (s *SmartContract) QuerySuperviser(ctx contractapi.TransactionContextInterface, id string) (*Superviser, error) {

	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
		return nil, fmt.Errorf(ERROR_CODE_ILLEGALID)
	}

	superviserAsBytes, err := ctx.GetStub().GetState(id)

	if err != nil {
		return nil, fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}

	if superviserAsBytes == nil {
		return nil, fmt.Errorf(ERROR_CODE_UNEXISTDATA)
	}

	superviser := new(Superviser)
	_ = json.Unmarshal(superviserAsBytes, superviser)

	return superviser, nil
}

// ChangeSuperviserName updates the name field of superviser with given id in the world state
func (s *SmartContract) ChangeSuperviserName(ctx contractapi.TransactionContextInterface, id string, newname string) error {

	superviser, err := s.QuerySuperviser(ctx, id)

	if err != nil {
		return err
	}
	if superviser.State == ACCOUNT_STATE_INVALID {
		return fmt.Errorf(ERROR_CODE_USERINVALID)
	}

	superviser.Name = newname
	superviser.LastUpdateTime = strconv.FormatInt(time.Now().Unix(), 10)

	superviserAsBytes, _ := json.Marshal(superviser)

	return ctx.GetStub().PutState(superviser.Id, superviserAsBytes)
}

// DeleteSuperviser delete the superviser with the id given in world state
func (s *SmartContract) DeleteSuperviser(ctx contractapi.TransactionContextInterface, id string) error {
	if !strings.HasPrefix(id, PREFIX_ID_SUPERVISER) {
		return fmt.Errorf(ERROR_CODE_ILLEGALID)
	}
	return ctx.GetStub().DelState(id)
}
