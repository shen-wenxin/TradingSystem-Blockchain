# fabric-gateway-java 调用服务器上部署的链码

## 服务器端

### 链代码部署

下载`fabric-samples`(github自行搜索)

将链代码拷贝至`fabric-samples/asset-transfer-basic/chaincode-my/contract`

#### 安装依赖项

```
GO111MODULE=on go mod vendor
```

#### 启动区块链网络

导航入 test-network 

```
cd fabric-samples/test-network
```

为了防止之前的网络没有完全关闭

```
./network.sh down
```

启动Fabric 测试网络

```
./network.sh up createChannel -c mychannel -ca
```

将链代码部署

```
./network.sh deployCC -ccn basic -ccp ../../TradingSystem-Blockchain/chaincode/contract -ccl go
```

当部署成功后，会有以下提示：

```
Committed chaincode definition for chaincode 'basic' on channel 'mychannel':
Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc, Approvals: [Org1MSP: true, Org2MSP: true]
Query chaincode definition successful on peer0.org2 on channel 'mychannel'
Chaincode initialization is not required
```

## 本地Springboot

结构：

```
|-TradingSystem
	|-src
		|-main
			|-java\com\example\tradingSystem
			|-resourcse
				|-cypto-config
				|-fabric.config.properties
				|-networkConnection.json
```

1. 从服务器的`/fabric-samples/test-network/organizations`目录中将`ordererOrganizations`和`peerOrganizations` 复制出来，放到resource/cypto-config文件夹下。

2. 根据需要修改链接配置文件`networkConnection.json`[参考](https://github.com/hyperledger/fabric-gateway-java/blob/main/src/test/java/org/hyperledger/fabric/gateway/connection-tls.json)，特别是ip跟端口号，需要自行修改

   

