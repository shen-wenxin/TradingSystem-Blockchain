# Writing your first Application

## Application

应用程序可以通过向账本提交交易或查询账本内容来与区块链网络进行交互。

应用程序使用Fabric SDK与区块链网络进行交互。

应用程序必须遵循六个基本步骤来提交交易。

> ·选择一个身份
> 
> 
> ·连接到网关
> 
> ·访问所需网络
> 
> ·构建智能合约的交易请求
> 
> ·向网络提交交易
> 
> ·处理相应
> 

接下来的教程基于fabric-samples。

## Launch the network

导航进入test-network

```go
cd fabric-samples/test-network
```

如果有正在运行的 test network，先关掉

```go
./network.sh down
```

启动 test network

```go
./network.sh up createChannel -c mychannel -ca
```

这个命令将部署一个 具有两个对等点(peer)、一个排序服务(ordering service)和三个证书颁发机构(Orderer, Org1, Org2)。

- -ca 标志：说明了没有使用crptogen工具，而是使用了证书颁发机构启动测试网络。

接下来介绍如何部署**链代码**。

```go
./network.sh deployCC -ccn basic -ccp ../asset-transfer-basic/chaincode-javascript/ -ccl javascript
```

- Note: 该脚本可以用来打包、安装、查询已经安装的链码，批准Org1和Org2的链码，最后提交链码。

## Sample application

```jsx
cd asset-transfer-basic/application-javascript
npm install
node app.js
```

### First, the application enrolls the admin user

```jsx
async function main() {
  try {
    // build an in memory object with the network configuration (also known as a connection profile)
    const ccp = buildCCP();

    // build an instance of the fabric ca services client based on
    // the information in the network configuration
    const caClient = buildCAClient(FabricCAServices, ccp);

    // setup the wallet to hold the credentials of the application user
    const wallet = await buildWallet(Wallets, walletPath);

    // in a real application this would be done on an administrative flow, and only once
    await enrollAdmin(caClient, wallet);
```

将 CA administrator’s 证书存在 wallet 文件夹中。可以在wallet/admin.id 文件中找到私钥。

<aside>
💡 注册管理员和注册应用用户 是 应用程序和证书颁发机构之间的交互，而不是应用程序和链码之间的交互。如果决定通过关闭网络并重新启动网络开始，则必须在重新运行javascript应用程序之前删除钱包文件夹及其身份。发生这种情况是因为当测试网络被关闭时，证书颁发机构及其数据库被关闭，但原始钱包仍保留在 application-javascript 目录中，因此必须将其删除。

</aside>

### Second, the application registers and enrolls an application user

现在在钱包中有了管理员凭据，应用程序使用管理员用户来注册，并注册一个应用程序用户，该用户将用于与区块链网络进行交互。应用程序代码如下所示。

```jsx
// in a real application this would be done only when a new user was required to be added
// and would be part of an administrative flow
await registerAndEnrollUser(caClient, wallet, mspOrg1, org1UserId, 'org1.department1');
```

与管理员注册类似，此功能使用CSR注册和注册appUser，并将其凭据与管理员的凭据一起存储在钱包中。现在，我们的应用程序可以使用两个单独的用户(admin和appUser)的身份。

### Third, the sample application prepares a connection to the channel and smart contract

在前面的步骤中，应用程序生成了管理员和应用程序用户凭据并将它们放入钱包中。如果凭证存在并且具有与之关联的正确权限属性，则示例应用程序用户将能够在获得对通道名称和合约名称的引用后调用链代码函数。

在接下来的应用程序中，应用程序通过网关使用合约名称和通道名称获取对合约的引用。

```jsx
// Create a new gateway instance for interacting with the fabric network.
// In a real application this would be done as the backend server session is setup for
// a user that has been verified.
const gateway = new Gateway();

try {
  // setup the gateway instance
  // The user will now be able to create connections to the fabric network and be able to
  // submit transactions and query. All transactions submitted by this gateway will be
  // signed by this user using the credentials stored in the wallet.
  await gateway.connect(ccp, {
    wallet,
    identity: userId,
    discovery: {enabled: true, asLocalhost: true} // using asLocalhost as this gateway is using a fabric network deployed locally
  });

  // Build a network instance based on the channel where the smart contract is deployed
  const network = await gateway.getNetwork(channelName);

  // Get the contract from the network.
  const contract = network.getContract(chaincodeName);
```

当链码包含多个智能合约时，在getContract() API上，可以指定链码包的名称和要定位的特定智能合约。

```jsx
const contract = await network.getContract('chaincodeName', 'smartContractName');
```

### Fourth, the application initializes the ledger with some sample data

submitTransaction() 函数 被用来调用链码。

```jsx
// Initialize a set of asset data on the channel using the chaincode 'InitLedger' function.
// This type of transaction would only be run once by an application the first time it was started after it
// deployed the first time. Any updates to the chaincode deployed later would likely not need to run
// an "init" type function.
console.log('\n--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger');
await contract.submitTransaction('InitLedger');
console.log('*** Result: committed');
```

### Fifth, the application invokes each of the chaincode functions

首先，关于查询账本。

区块链网络中的每个peer都托管一份账本副本。应用程序可以使用在对等节点上运行的智能合约只读调用方法（查询方法）来查看分类账中的最新数据。

当您想查询单个peer的数据而不向排序服务提交交易时，可以使用evaluateTransaction()

```jsx
// Let's try a query type operation (function).
// This will be sent to just one peer and the results will be shown.
console.log('\n--> Evaluate Transaction: GetAllAssets, function returns all the current assets on the ledger');
let result = await contract.evaluateTransaction('GetAllAssets');
console.log(`*** Result: ${prettyJSONString(result.toString())}`);
```

当需要改变链上的数据的时候

```jsx
// Now let's try to submit a transaction.
// This will be sent to both peers and if both peers endorse the transaction, the endorsed proposal will be sent
// to the orderer to be committed by each of the peer's to the channel ledger.
console.log('\n--> Submit Transaction: CreateAsset, creates new asset with ID, color, owner, size, and appraisedValue arguments');
await contract.submitTransaction('CreateAsset', 'asset13', 'yellow', '5', 'Tom', '1300');
console.log('*** Result: committed');
```

### clean up
```jsx
./network.sh down
```
