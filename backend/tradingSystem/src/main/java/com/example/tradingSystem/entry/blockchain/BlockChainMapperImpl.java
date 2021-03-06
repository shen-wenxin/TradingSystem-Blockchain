package com.example.tradingSystem.entry.blockchain;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson2.JSON;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.Commodity.Commodity;
import com.example.tradingSystem.domain.Trade.Account;
import com.example.tradingSystem.domain.Trade.Trade;
import com.example.tradingSystem.domain.User.Business;
import com.example.tradingSystem.domain.User.Customer;
import com.example.tradingSystem.domain.User.Superviser;
import com.example.tradingSystem.web.exception.BussinessException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class BlockChainMapperImpl implements BlockChainMapper{

    public Contract contract;

    BlockChainMapperImpl() throws Exception{
        try {
            InputStream inputStream = BlockChainMapperImpl.class.getResourceAsStream("/fabric.config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            String networkConfigPath = properties.getProperty("networkConfigPath");
            String channelName = properties.getProperty("channelName");
            String contractName = properties.getProperty("contractName");

            //??????org1??????user1?????????????????????wallet????????????????????????

            String certificatePath = properties.getProperty("certificatePath");
            X509Certificate certificate = readX509Certificate(Paths.get(certificatePath));

            String privateKeyPath = properties.getProperty("privateKeyPath");
            PrivateKey privateKey = getPrivateKey(Paths.get(privateKeyPath));

            Wallet wallet = Wallets.newInMemoryWallet();
            wallet.put("user1",Identities.newX509Identity("Org1MSP",certificate,privateKey));

            //??????connection.json ??????Fabric??????????????????
            Gateway.Builder builder = Gateway.createBuilder()
            .identity(wallet, "user1")
            .networkConfig(Paths.get(networkConfigPath));

            //????????????
            Gateway gateway = builder.connect();
            //????????????
            Network network = gateway.getNetwork(channelName);
            //??????????????????
            this.contract = network.getContract(contractName);
            log.info("Init Fabric Contract finished.");
        } catch(Exception e){
            log.error("Init Fabric Contract Failed,{}",e.getMessage());
            e.printStackTrace();
        }
    }

    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(privateKeyReader);
        }
    }   

    /* Superviser */
    @Override
    public Superviser getSuperviser(String id) {
        
        log.info("Begin to get superviser details from blockchain. id: " + id);
        if (id.isEmpty()){
            log.error("id cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }

        //id ??????SuperViser?????????
        id = Constant.PREFIX_ID_SUPERVISER + id;

        try{
            log.info("[Fabric]Evaluate Transaction: QuerySuperviser");
            String result = new String(this.contract.evaluateTransaction("QuerySuperviser", id));
            log.info("[Fabric]QuerySuperviser Succeed.");
            Superviser superviser = JSON.parseObject(result, Superviser.class);
            log.info("[BlockChainMapperImpl]Parse string to Superviser object succeed.");
            return superviser;

        }catch (Exception e){
            log.error("[BlockChainMapperImpl]QuerySuperviser falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }

    }

    @Override
    public Boolean createSuperviser(String acount, String name, String phone){
        if (acount.isEmpty() || name.isEmpty()){
            log.error("id or name cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        String id = Constant.PREFIX_ID_SUPERVISER + acount;
        try{
            contract.submitTransaction("CreateSuperviser", id, name, phone);
            log.info("[Fabric]createSuperviser Succeed.");
            return true;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]createSuperviser falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }

    }


    /*Customer */
    @Override
    public Customer getCustomer(String id) {
        if (id.isEmpty()){
            log.error("id cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        // id ??????Customer ?????????
        id = Constant.PREFIX_ID_CUSTOMER + id;
        try{
            String result = new String(this.contract.evaluateTransaction("QueryCustomer", id));
            log.info("[Fabric]QueryCustomer Succeed.");
            Customer cus = JSON.parseObject(result, Customer.class);
            log.info("[BlockChainMapperImpl]Parse string to Customer object succeed.");
            return cus;
        }catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryCustomer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }


    @Override
    public Boolean createCustomer(String id, String name, String phone) {
        if (id.isEmpty() || name.isEmpty()){
            log.error("id or name cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        id = Constant.PREFIX_ID_CUSTOMER + id;
        try{
            contract.submitTransaction("CreateCustomer", id, name, phone);
            log.info("[Fabric]createCustomer Succeed.");
            return true;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]createCustomer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }


    @Override
    public Business getBusiness(String id) {
        if (id.isEmpty()){
            log.error("id cannot be null.");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        // id ??????Business ?????????
        id = Constant.PREFIX_ID_BUSSINIESSMAN + id;
        try{
            String result = new String(this.contract.evaluateTransaction("QueryBusiness", id));
            log.info("[Fabric]QueryBusiness Succeed.");
            Business bus = JSON.parseObject(result, Business.class);
            log.info("[BlockChainMapperImpl]Parse string to Business object succeed.");
            return bus;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryBusiness falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());

        }
    }

    @Override
    public Boolean createBusiness(String id, String name, String phone) {
        if (id.isEmpty() || name.isEmpty()){
            log.error("id and name cannot be null");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        // ??????Business ??????
        id = Constant.PREFIX_ID_BUSSINIESSMAN + id;
        try{
            this.contract.submitTransaction("CreateBusiness", id, name, phone);
            log.info("[Fabric]createBusiness Succeed.");
            return true;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]createBusiness falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public Boolean DataOnChainCheck(String id) {
        try{
            String result = new String(this.contract.evaluateTransaction("DataOnChainCheck", id));
            log.info("[Fabric]DataOnChainCheck Succeed.result: {}",result);
            if (result.equals("false")){
                return false;
            }
            return true;
            
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]DataOnChainCheck falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());

        }
    }
    @Override
    public Boolean ExistAccountCheck(String userId, String month, String year) {
        try{
            String result = new String(this.contract.evaluateTransaction("ExistAccountByUserMonth", userId, month, year));
            log.info("[Fabric]ExistAccountByUserMonth Succeed.result: {}",result);
            if (result.equals("false")){
                return false;
            }
            return true;
            
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]ExistAccountByUserMonth falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());

        }
    }


    @Override
    public Boolean createCommodity(String name, String price, String issuer, String issuerName) {
        if (! issuer.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN)){
            // ????????????
            log.error("id param wrong");
            throw new BussinessException(Status.FAIL_NO_PERMISSION.code());

        }
        // ????????????id
        String random =String.valueOf((int)((Math.random()*9+1)*1000)); //?????? 4????????????
        String id = Constant.PREFIX_ID_COMMIDITY + random + issuer;

        try{
            this.contract.submitTransaction("CreateCommodity", id, name, price, issuer, issuerName);
            log.info("[Fabric]CreateCommodity Succeed.");
            return true;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]CreateCommodity falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());

        }

    }

    @Override
    public List<Commodity> getCommodityOnSaleByIssuer(String issuer) {
        log.info("getCommodityOnSaleByIssuer + and id is "+issuer);
        if (!issuer.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN)){
            log.error("id param wrong");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        try{
            String result = new String(this.contract.evaluateTransaction("QueryCommodityOnSaleByIssuer", issuer));
            log.info("[Fabric]QueryCommodityOnSaleByIssuer Succeed.");
            List<Commodity> res = new ArrayList<>();
            res = JSON.parseArray(result, Commodity.class);
            log.info("[BlockChainMapperImpl]Parse string to Commodity object succeed.");
            return res;

        }catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryCommodityOnSaleByIssuer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public List<Commodity> getCommodityOnSale() {
        try{
            String result = new String(this.contract.evaluateTransaction("QueryCommodityOnSale"));
            log.info("[Fabric]QueryCommodityOnSale Succeed.");
            List<Commodity> res = new ArrayList<>();
            res = JSON.parseArray(result, Commodity.class);
            log.info("[BlockChainMapperImpl]Parse string to Commodity object succeed.");
            return res;
        }catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryCommodityOnSale falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public Boolean createBuyTrade(String tTime, String price, String bId, String sId, String cId) {
        // tTime tradeTime, bId buyerId(customerId), sId salerId(bussinessId), cId commodityId

        // Id ??????
        if ((!bId.startsWith(Constant.PREFIX_ID_CUSTOMER)) || 
            (!sId.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN))||
            (!cId.startsWith(Constant.PREFIX_ID_COMMIDITY))){
                log.error("id param wrong");
                throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }

        // ??????cid
        String random = String.valueOf((int)((Math.random()*9+1)*1000));// ??????4????????????
        // tId ?????? ?????? + ???????????? + 4????????????
        String tId = Constant.PREFIX_ID_TRADE + tTime + random;
        try{
            this.contract.submitTransaction("CreateBuyTrade", tId, tTime, price, bId, sId, cId);
            log.info("[Fabric]CreateBuyTrade succeed");
            return true;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]createBuyTrade falied." + e.getMessage());
            throw new BussinessException(Status.TRADE_SERVICE_FAILED.code());
        }

    }
    
    @Override
    public List<Commodity> getCommodityBaughtByCus(String customer) {
        if (! customer.startsWith(Constant.PREFIX_ID_CUSTOMER)){
            log.info("id param wrong");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }

        try{
            String result = new String(this.contract.evaluateTransaction("QueryAllCommoditybeBaughtByCustomer", customer));
            log.info("[Fabric]QueryAllCommoditybeBaughtByCustomer Succeed.");
            List<Commodity> res = new ArrayList<>();
            res = JSON.parseArray(result, Commodity.class);
            log.info("[BlockChainMapperImpl]Parse string to Commodity object succeed.");
            return res;
        }catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryAllCommoditybeBaughtByCustomer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public List<Commodity> getCommoditySaledByIssuer(String bus) {
        if (! bus.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN)){
            log.info("id param wrong");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        try {
            String result = new String(this.contract.evaluateTransaction("QueryCommoditySaledByIssuer", bus));
            log.info("[Fabric]QueryCommoditySaledByIssuer Succeed.");
            List<Commodity> res = new ArrayList<>();
            res = JSON.parseArray(result, Commodity.class);
            log.info("[BlockChainMapperImpl]Parse string to Commodity object succeed.");
            return res;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryCommoditySaledByIssuer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());

        }
    }

    @Override
    public Business getBusinessById(String businessId) {
        if (! businessId.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN)){
            log.info("id param wrong");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        try{
            String result = new String(this.contract.evaluateTransaction("QueryBusiness", businessId));
            log.info("[Fabric]QueryBusiness Succeed. result : "+ result);
            Business bus = JSON.parseObject(result, Business.class);
            log.info("[BlockChainMapperImpl]Parse string to Business object succeed.");
            return bus;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryBusiness falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());

        }
    }

    @Override
    public Integer getBusProfitByMonth(String bId) {
        if (! bId.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN)){
            log.info("id param wrong");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        try{
            
		    Calendar calendar = Calendar.getInstance();
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            log.info("year is " + year + ", month is "+ month);
            String result = new String(this.contract.evaluateTransaction("GetBusProfitByMonth", bId, month,year));
            log.info("[Fabric]GetBusProfitByMonth Succeed. result : "+ result);
            Integer res = Integer.parseInt(result);
            log.info("[BlockChainMapperImpl]Parse string to int object succeed.");
            return res;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryBusiness falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());

        }
    }

    @Override
    public Integer getCusSpentByMonth(String cId) {
        if (! cId.startsWith(Constant.PREFIX_ID_CUSTOMER)){
            log.info("id param wrong");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        try{
            
		    Calendar calendar = Calendar.getInstance();
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            log.info("year is " + year + ", month is "+ month);
            String result = new String(this.contract.evaluateTransaction("GetCusProfitByMonth", cId, month,year));
            log.info("[Fabric]GetCusProfitByMonth Succeed. result : "+ result);
            Integer res = Integer.parseInt(result);
            log.info("[BlockChainMapperImpl]Parse string to int object succeed.");
            return res;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]GetCusProfitByMonth falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }



    @Override
    public Account getAccountByUserMonth(String userId, String month, String year) {
        try{
            String result = new String(this.contract.evaluateTransaction("QueryAccountByUserMonth", userId, month, year));
            log.info("[Fabric]QueryAccountByUserMonth Succeed. result : "+ result);
            Account acc = JSON.parseObject(result, Account.class);
            log.info("[BlockChainMapperImpl]Parse string to Account object succeed.");
            return acc;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryAccountByUserMonth falied." + e.getMessage());
            throw new BussinessException(Status.JOB_ERROR.code());

        }
    }

    @Override
    public Customer getCustomerById(String id) {
        if (! id.startsWith(Constant.PREFIX_ID_CUSTOMER)){
            log.info("id param wrong");
            throw new BussinessException(Status.FAIL_INVALID_PARAM.code());
        }
        try{
            String result = new String(this.contract.evaluateTransaction("QueryCustomer", id));
            log.info("[Fabric]QueryCustomer Succeed.");
            Customer cus = JSON.parseObject(result, Customer.class);
            log.info("[BlockChainMapperImpl]Parse string to Customer object succeed.");
            return cus;
        }catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryCustomer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public List<Business> getAllBusiness() {
        try {
            String result = new String(this.contract.evaluateTransaction("QueryAllBussniss"));
            log.info("[Fabric]QueryAllBussniss Succeed.");
            List<Business> res = new ArrayList<>();
            res = JSON.parseArray(result, Business.class);
            log.info("[BlockChainMapperImpl]Parse string to Business object succeed.");
            return res;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryAllBussniss falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
        
    }

    @Override
    public List<Customer> getAllCustomers() {
        try {
            String result = new String(this.contract.evaluateTransaction("QueryAllCustomer"));
            log.info("[Fabric]QueryAllCustomer Succeed.");
            List<Customer> res = new ArrayList<>();
            res = JSON.parseArray(result, Customer.class);
            log.info("[BlockChainMapperImpl]Parse string to Customer object succeed.");
            return res;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryAllCustomer falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public List<Commodity> getAllComodity() {
        try {
            String result = new String(this.contract.evaluateTransaction("QueryAllCommodity"));
            log.info("[Fabric]QueryAllCommodity Succeed.");
            List<Commodity> res = new ArrayList<>();
            res = JSON.parseArray(result, Commodity.class);
            log.info("[BlockChainMapperImpl]Parse string to Commodity object succeed.");
            return res;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryAllCommodity falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public List<Trade> getAllTrade() {
        try {
            String result = new String(this.contract.evaluateTransaction("QueryAllTrade"));
            log.info("[Fabric]QueryAllTrade Succeed.");
            List<Trade> res = new ArrayList<>();
            res = JSON.parseArray(result, Trade.class);
            log.info("[BlockChainMapperImpl]Parse string to trade object succeed.");
            return res;
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]QueryAllTrade falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
    }

    @Override
    public void monthlyAccountCheck() {
        try{
            Calendar calendar = Calendar.getInstance();
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
            log.info("[Fabric]MonthlyAccountCheck, "+ month+" "+year);
            contract.submitTransaction("MonthlyAccountCheck",month, year);
            log.info("[Fabric]MonthlyAccountCheck Succeed.");
        } catch (Exception e){
            log.error("[BlockChainMapperImpl]MonthlyAccountCheck falied." + e.getMessage());
            throw new BussinessException(Status.BLOCKCHAIN_SERVICE_FAILED.code());
        }
        
    }

    



    



    

    

    

   

   





}
