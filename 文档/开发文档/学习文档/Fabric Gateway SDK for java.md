# Fabric Gateway SDK for java

Fabric Gateway 客户端API允许应用程序与Hyperledger Fabric区块链网络进行交互。它实现了Fabric交易模型，提供了一个简单的API来将交易提交到账本，或以最少的代码查询账本的内容。

## 使用方法

以 fabric-samples 仓库为例。

## 环境配置

1、安装jdk。

```jsx
sudo apt-get install openjdk-11-jdk
```

2、安装SDKMAN

```jsx
curl -s "https://get.sdkman.io" | bash
```

3、安装gradle

```jsx
sdk install gradle 7.4.1
```

## 安装链代码

```jsx
cd fabric-samples/asset-transfer-basic/chaincode-go
```

为安装依赖项，请在asset-transfer-basic/chaincode-go 文件夹中输入

```jsx
GO111MODULE=on go mod vendor
```

## 启动区块链网络

导航入 test-network 

```jsx
cd fabric-samples/test-network
```

为了防止之前得网络没有完全关闭

```jsx
./network.sh down
```

启动Fabric 测试网络

```jsx
./network.sh up createChannel -c mychannel -ca
```

然后将链代码部署上去

```jsx
./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-go/ -ccl go
```

当部署成功后，会有以下显示：

```jsx
Committed chaincode definition for chaincode 'basic' on channel 'mychannel':
Version: 1.0, Sequence: 1, Endorsement Plugin: escc, Validation Plugin: vscc, Approvals: [Org1MSP: true, Org2MSP: true]
Query chaincode definition successful on peer0.org2 on channel 'mychannel'
Chaincode initialization is not required
```

## sample application

编译

```jsx
gradle build
```

直接运行项目

```jsx
gradle run
```