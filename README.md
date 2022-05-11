# TradingSystem-Blockchain
基于区块链的交易系统设计（v1.0）

## 项目介绍

​    该项目尝试将区块链技术应用到 **交易系统** 中。尝试基于区块链技术，构建一个商品交易网络来进行商品的发售、购买等。

![https://gitee.com/shen_wenxin0510/readme-pictures/raw/master/notes/CommodityNet.png](https://gitee.com/shen_wenxin0510/readme-pictures/raw/master/notes/CommodityNet.png)

​    计划充分利用区块链 *安全、不可篡改、去中心化* 的特性，来完成该交易系统的设计。将商家、消费者、商品、交易等信息存储在区块链上，以达到防伪溯源的目的。由于该系统仅为探索性项目，不涉及真实交易，故暂不对测试数据进行脱敏处理。

## 项目结构

### 文件结构

```jsx
TradingSystem-BlockChain
	|-frontend # 前端
	|-backend # 后端
	|-chaincode # 链代码
	|-doc # 相关文档

```

详细结构请参考doc/文件夹下相关文档。

### 前端展示

![https://gitee.com/shen_wenxin0510/readme-pictures/raw/master/home.png](https://gitee.com/shen_wenxin0510/readme-pictures/raw/master/home.png)

我知道很丑…但是半路出家学的vue，之后再多多学习一边学习一边优化吧（

## 依赖

### 前端

- vue@2.6.14

### 后端

- java version 11
- Gradle 7.4.2
- springboot 2.6.6
- fabric-gateway-java 2.2.0

### 链代码

- fabric-contract-api-go

## 运行

### 前端

```
npm install
npm run serve
```

### 后端

```
gradle bootRun
```

### 链代码

[链代码部署方式](https://github.com/shen-wenxin/TradingSystem-Blockchain/blob/dev/doc/%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/%E5%AD%A6%E4%B9%A0%E6%96%87%E6%A1%A3/%E5%A6%82%E4%BD%95%E9%83%A8%E7%BD%B2%E9%93%BE%E4%BB%A3%E7%A0%81.md)

