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
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.example.tradingSystem.common.Constant;
import com.example.tradingSystem.common.Status;
import com.example.tradingSystem.domain.Commodity.Commodity;
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
import net.bytebuddy.implementation.bind.annotation.Super;
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

            //使用org1中的user1初始化一个网关wallet账户用于连接网络

            String certificatePath = properties.getProperty("certificatePath");
            X509Certificate certificate = readX509Certificate(Paths.get(certificatePath));

            String privateKeyPath = properties.getProperty("privateKeyPath");
            PrivateKey privateKey = getPrivateKey(Paths.get(privateKeyPath));

            Wallet wallet = Wallets.newInMemoryWallet();
            wallet.put("user1",Identities.newX509Identity("Org1MSP",certificate,privateKey));

            //根据connection.json 获取Fabric网络连接对象
            Gateway.Builder builder = Gateway.createBuilder()
            .identity(wallet, "user1")
            .networkConfig(Paths.get(networkConfigPath));

            //连接网关
            Gateway gateway = builder.connect();
            //获取通道
            Network network = gateway.getNetwork(channelName);
            //获取合约对象
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

        //id 加入SuperViser的前缀
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
        // id 加入Customer 的前缀
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
        // id 加入Business 的前缀
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
        // 加入Business 前缀
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
    public Boolean createCommodity(String name, String price, String issuer, String issuerName) {
        if (! issuer.startsWith(Constant.PREFIX_ID_BUSSINIESSMAN)){
            // 身份校验
            log.error("非商家id");
            throw new BussinessException(Status.FAIL_NO_PERMISSION.code());

        }
        // 生成商品id
        String random =String.valueOf((int)((Math.random()*9+1)*100000)); //生成 6位随机数
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

    

    

    

   

   





}
