package main

import (
	"encoding/json"
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// 记账 核算
type Account struct {
	Id      string `json:"Id"`      // 账目id号
	UserId  string `json:"userId"`  // 该账目所属用户id号
	Month   string `json:"month"`   // 月份
	Year	string `json:"year"`	// 年份
	Income  int64 `json:"income"`  // 收入
	Outcome int64 `json:"outcome"` // 支出
	Total   int64 `json:"total"`   // 总计
}

const (
	INDEXNAME_USER_YEAR_MONTH_ID = "user~month~id"
)

const(
	BALANCE_ERROR = 1
)



func (s *SmartContract) QueryAccountByUserMonth(ctx contractapi.TransactionContextInterface,
	user string, month string, year string) (*Account, error, bool) {
	// 返回该用户上一月的账单
	resultIterator, err := ctx.GetStub().GetStateByPartialCompositeKey(INDEXNAME_USER_YEAR_MONTH_ID, []string{user, year, month})
	if err != nil {
		return nil, err, false
	}
	defer resultIterator.Close()

	result := new(Account)
	for resultIterator.HasNext() {
		item, _ := resultIterator.Next()
		_, compositeKeyParts, err := ctx.GetStub().SplitCompositeKey(item.Key)

		if err != nil {
			return nil, err,false
		}
		id := compositeKeyParts[3]

		accountAsBytes, err := ctx.GetStub().GetState(id)
		if err != nil {
			return nil, err, false
		}
		_ = json.Unmarshal(accountAsBytes, result)
		return result, nil, true
	}
	return nil, nil, false
}

// 月度账单核算
func (s *SmartContract) MonthlyAccountCheck(ctx contractapi.TransactionContextInterface, month string, year string) error {
	// 核算 商家数据
	err := s.MonthlyBusAccountCheck(ctx, month, year)
	if err != nil{
		return err
	}

	// 核算 用户数据

	err = s.MonthlyCusAccountCheck(ctx, month, year)
	if err != nil{
		return err
	}
	return nil
}

func (s *SmartContract) MonthlyCusAccountCheck(ctx contractapi.TransactionContextInterface, month string, year string) error {
	cusAll, err := s.QueryAllCustomer(ctx)

	if err != nil{
		return err
	}

	for _, cus := range cusAll{
		flag, err := s.MonthlyoneCusAccountCheck(ctx, cus, month, year)
		if err != nil{
			return err
		}
		if flag == false{
			return fmt.Errorf("data error, please check")
		}
	}
	return nil

}



func (s *SmartContract) MonthlyBusAccountCheck(ctx contractapi.TransactionContextInterface, month string, year string) error {
	busAll, err := s.QueryAllBussniss(ctx)
	if err != nil {
		return err
	}
	for _, bus := range busAll {
		flag, err := s.MonthlyoneBusAccountChect(ctx, bus, month, year)
		if err != nil{
			return err
		}
		if flag == false{
			return fmt.Errorf("data error, please check")
		}
	}
	return nil
}

func (s *SmartContract) MonthlyoneCusAccountCheck(ctx contractapi.TransactionContextInterface, 
	customer Customer,month string, year string) (bool, error) {
	cId := customer.Id

	lastTotal, err := s.getUserLastMonthAccount(ctx, cId, month, year)
	if err != nil {
		return false, err
	}
	trades, err := s.QueryMonthTradeByCus(ctx, cId, month, year)
	if err != nil {
		return false, err
	}

	newTotal := lastTotal
	for _, trade := range trades {
		price := trade.Price
		newTotal = newTotal - price
	}

	cusBalance, err := s.QueryCusBalance(ctx, cId)

	if err != nil{
		return false, err
	}

	// 检查误差
	if cusBalance - newTotal > BALANCE_ERROR{
		// 误差大,不符合要求
		return false, nil
	}
	return true, nil

}

func (s *SmartContract) MonthlyoneBusAccountChect(ctx contractapi.TransactionContextInterface,
	business Business, month string, year string) (bool, error) {
	//核查该用户数数据
	bId := business.Id

	lastTotal, err := s.getUserLastMonthAccount(ctx, bId, month, year)
	if err != nil {
		return false, err
	}
	trades, err := s.QueryMonthTradeByBusiness(ctx, bId, month, year)
	if err != nil{
		return false, nil
	}

	newTotal := lastTotal
	for _, trade := range trades {
		price := trade.Price
		newTotal = newTotal + price
	}

	// 得到bus balance
	busBalance, err := s.QueryBusBalance(ctx, bId)

	if err != nil{
		return false, err
	}

	// 检查误差
	if busBalance - newTotal > BALANCE_ERROR{
		// 误差大,不符合要求
		return false, nil
	}
	return true, nil

}

func (s *SmartContract) getUserLastMonthAccount(ctx contractapi.TransactionContextInterface,
	id string, month string, year string) (int64, error) {
	monthInt, _ := strconv.Atoi(month)
	yearInt, _ := strconv.Atoi(year)

	if monthInt == 1 {
		//倒推一年
		monthInt = 12
		yearInt = yearInt - 1
	} else {
		monthInt = monthInt - 1
	}
	month = strconv.FormatInt(int64(monthInt), 10)
	year = strconv.FormatInt(int64(yearInt), 10)
	account, err, exist := s.QueryAccountByUserMonth(ctx, id, month, year)
	if err != nil {
		return 0, err
	}
	if exist == false{
		// 不存在上个月的账单
		return 0, nil
	}
	return account.Total, nil

}


