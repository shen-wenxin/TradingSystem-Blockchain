package com.example.tradingSystem;

import com.example.tradingSystem.service.FabricService;

import org.hyperledger.fabric.gateway.Contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TradingSystemApplication {
	public static FabricService fService;

	

	public static void main(String[] args) throws Exception {
		initFabricService();
		SpringApplication.run(TradingSystemApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/fabric")
	public String fabric() {
		try {
			Contract contract = fService.getContract();
			byte[] result;
			System.out.println("Submit Transaction: InitLedger creates the initial set of assets on the ledger.");
			contract.submitTransaction("InitLedger");

			System.out.println("\n");
			result = contract.evaluateTransaction("GetAllAssets");
			System.out.println("Evaluate Transaction: GetAllAssets, result: " + new String(result));

			System.out.println("\n");
			System.out.println("Submit Transaction: CreateAsset asset13");
			//CreateAsset creates an asset with ID asset13, color yellow, owner Tom, size 5 and appraisedValue of 1300
			contract.submitTransaction("CreateAsset", "asset13", "yellow", "5", "Tom", "1300");

			System.out.println("\n");
			System.out.println("Evaluate Transaction: ReadAsset asset13");
			// ReadAsset returns an asset with given assetID
			result = contract.evaluateTransaction("ReadAsset", "asset13");
			System.out.println("result: " + new String(result));

			System.out.println("\n");
			System.out.println("Evaluate Transaction: AssetExists asset1");
			// AssetExists returns "true" if an asset with given assetID exist
			result = contract.evaluateTransaction("AssetExists", "asset1");
			System.out.println("result: " + new String(result));

			System.out.println("\n");
			System.out.println("Submit Transaction: UpdateAsset asset1, new AppraisedValue : 350");
			// UpdateAsset updates an existing asset with new properties. Same args as CreateAsset
			contract.submitTransaction("UpdateAsset", "asset1", "blue", "5", "Tomoko", "350");

			System.out.println("\n");
			System.out.println("Evaluate Transaction: ReadAsset asset1");
			result = contract.evaluateTransaction("ReadAsset", "asset1");
			System.out.println("result: " + new String(result));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.format("Hello ");
	}

	private static void initFabricService() throws Exception{
		fService = new FabricService();
	}

	public static FabricService getFabricService(){
		return fService;
	}

}