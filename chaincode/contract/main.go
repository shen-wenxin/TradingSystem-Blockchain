package main

import (
    "github.com/hyperledger/fabric-contract-api-go/contractapi"
)

func main() {
    simpleContract := new(SimpleContract)

    cc, err := contractapi.NewChaincode(simpleContract)

    if err != nil {
        panic(err.Error())
    }

    if err := cc.Start(); err != nil {
        panic(err.Error())
    }
}