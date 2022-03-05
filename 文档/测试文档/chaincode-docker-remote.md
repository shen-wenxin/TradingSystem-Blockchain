# Test chaincode as developer

以开发者的身份测试链代码

参考链接：[tutorials](https://github.com/hyperledger/fabric-contract-api-go/blob/main/tutorials/getting-started.md)

## Prerequisites

- [fabric-samples 2.2](https://github.com/hyperledger/fabric-samples)
- [Docker](https://docs.docker.com/install/) 

- [Docker compose](https://docs.docker.com/compose/install/)

具体的版本号视具体情况而定，该项目：

```cmd
$ docker --version
Docker version 20.10.12
$ docker-compose version
Docker Compose version v2.2.2
```

请注意，虽然该项目使用的是v2.2版本的fabric，但是`fabric-samples v1.4`中的开发者模式仍然适用。请单独下载`fabric-samples v1.4`中的`chaincode-docker-devmode`,放入``fabric-samples`目录下。

## 目录结构

```go
fabric-samples
	|-chaincode
		|-请在这个文件夹之下放链代码(e.g. contract)
   	|-chaincode-docker-devmode
```

## 测试

进入`chaincode-docker-devmode`. 这个文件夹提供了一个 docker-compose文件，定义了一个简单的结构网络，我们将在该网络上运行我们的链代码。

```cmd
$ cd fabric-samples/chaincode-docker-devmode
```

启动网络

> 注意：该命令会连续打印输出，不会退出

```cmd
$ docker-compose -f docker-compose-simple.yaml up
```

### 运行链代码

开启一个新的命令行窗口，并且进入``chaincode-docker-devmode`文件夹. 由于我的链代码的所有者为root，我需要用root权限去更改，所以我用root的身份启动docker

```cmd
$ docker exec -it --user root chaincode sh
$ cd contract
# 打包代码
$ go mod vendor
$ go build
# 运行代码
$ CORE_CHAINCODE_ID_NAME=mycc:0 CORE_PEER_TLS_ENABLED=false ./contract -peer.address peer:7052
```

### 交互

开启新的命令行窗口

```cmd
$ docker exec -it cli sh
# 安装链代码
$ peer chaincode install -p ./chaincode/contract -n mycc -v 0
# 实例化
$ peer chaincode instantiate -n mycc -v 0 -c '{"Args":[]}' -C myc
# 一旦链码被实例化，你就可以发出交易来调用链码中的合约函数。
$ peer chaincode invoke -n mycc -c '{"Args":["函数名", "参数1", "参数2", ...]}' -C myc
# 查询(不涉及链上数据变化)
$ peer chaincode query -n mycc -c '{"Args":["函数名", "参数1"]}' -C myc
```

