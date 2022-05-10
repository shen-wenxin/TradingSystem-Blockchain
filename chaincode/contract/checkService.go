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
	Year    string `json:"year"`    // 年份
	Income  int64  `json:"income"`  // 收入
	Outcome int64  `json:"outcome"` // 支出
	Total   int64  `json:"total"`   // 总计
}

const (
	INDEXNAME_ACCOUNT_USER_YEAR_MONTH_ID = "account~user~year~month~id"
	INDEXNAME_ACCOUNT_USER_ID = "account~user~id"
)

const (
	BALANCE_ERROR = 1
)

func (s *SmartContract) QueryAccountByUserMonth(ctx contractapi.TransactionContextInterface,
	user string, month string, year string) (*Account, error) {
	// 返回该用户上一月的账单
	accountId := PREFIX_ID_ACCOUNT + user + month + year
	accountAsBytes, err := ctx.GetStub().GetState(accountId)
	if err != nil {
		return nil, fmt.Errorf(ERROR_CODE_GETCHAINFAILED)
	}
	if accountAsBytes == nil {
		return nil, nil
	}
	acount := new(Account)
	_ = json.Unmarshal(accountAsBytes, acount)
	return acount, nil

}

func (s *SmartContract) ExistAccountByUserMonth(ctx contractapi.TransactionContextInterface,
	user string, month string, year string)(bool, error){
	accountId := PREFIX_ID_ACCOUNT + user + month + year
	accountAsBytes, err := ctx.GetStub().GetState(accountId)
	if err != nil {
		return false, err
	}
	if accountAsBytes == nil {
		return false, nil
	}
	return true, nil
}

// 月度账单核算
func (s *SmartContract) MonthlyAccountCheck(ctx contractapi.TransactionContextInterface, month string, year string) error {
	// 核算 商家数据
	err := s.MonthlyBusAccountCheck(ctx, month, year)
	if err != nil {
		return err
	}

	// 核算 买家数据

	err = s.MonthlyCusAccountCheck(ctx, month, year)
	if err != nil {
		return err
	}
	return nil
}

func (s *SmartContract) MonthlyCusAccountCheck(ctx contractapi.TransactionContextInterface, month string, year string) error {
	cusAll, err := s.QueryAllCustomer(ctx)

	if err != nil {
		return err
	}

	for _, cus := range cusAll {
		flag, err := s.MonthlyoneCusAccountCheck(ctx, cus, month, year)
		if err != nil {
			return err
		}
		if flag == false {
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
		if err != nil {
			return err
		}
		if flag == false {
			return fmt.Errorf("data error, please check")
		}
	}
	return nil
}

func (s *SmartContract) MonthlyoneCusAccountCheck(ctx contractapi.TransactionContextInterface,
	customer Customer, month string, year string) (bool, error) {
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

	if err != nil {
		return false, err
	}

	// 检查误差
	if cusBalance-newTotal > BALANCE_ERROR {
		// 误差大,不符合要求
		return false, nil
	}
	income := 0
	outcome := lastTotal - newTotal

	

	// 通过检查，生成该月的核算数据
	err = s.initAccountMonthly(ctx, cId, month, year, income, int(outcome), int(newTotal))
	if err != nil{
		return false, err
	}
	return true, nil

}

func (s *SmartContract) initAccountMonthly(ctx contractapi.TransactionContextInterface,
	userId string, month string, year string, income int, outcome int, total int) error {
	accountId := PREFIX_ID_ACCOUNT + userId + month + year
	account := Account{
		Id:      accountId,
		UserId:  userId,
		Month:   month,
		Year:    year,
		Income:  int64(income),
		Outcome: int64(outcome),
		Total:   int64(total),
	}

	accountAsBytes,_ := json.Marshal(account) 
	err := ctx.GetStub().PutState(account.Id, accountAsBytes)

	if err != nil {
		return fmt.Errorf(ERROR_CODE_PUTCHAINFAILED)
	}

	// 开始创建复合键
	err = s.createCompositeKeyandSave(ctx, INDEXNAME_ACCOUNT_USER_YEAR_MONTH_ID, []string{account.UserId, account.Year, account.Month, account.Id})
	if err != nil{
		return err
	}

	err = s.createCompositeKeyandSave(ctx, INDEXNAME_ACCOUNT_USER_ID, []string{account.UserId, account.Id})

	if err != nil{
		return err
	}
	return nil

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
	if err != nil {
		return false, nil
	}

	newTotal := lastTotal
	for _, trade := range trades {
		price := trade.Price
		newTotal = newTotal + price
	}

	// 得到bus balance
	busBalance, err := s.QueryBusBalance(ctx, bId)

	if err != nil {
		return false, err
	}

	// 检查误差
	if busBalance-newTotal > BALANCE_ERROR {
		// 误差大,不符合要求
		return false, nil
	}

	income := newTotal - lastTotal
	outcome := 0

	// 通过检查，生成该月的核算数据
	err = s.initAccountMonthly(ctx, bId, month, year, int(income), int(outcome), int(newTotal))
	if err != nil{
		return false, err
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
	account, err := s.QueryAccountByUserMonth(ctx, id, month, year)
	if err != nil {
		return 0, err
	}
	if account == nil {
		// 不存在上个月的账单
		return 0, nil
	}
	return account.Total, nil

}
