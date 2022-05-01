package com.example.tradingSystem;

import com.example.tradingSystem.domain.User.User;
import com.example.tradingSystem.entry.user.UserMapper;
import com.example.tradingSystem.service.FabricService;
import com.example.tradingSystem.service.user.UserService;

import org.hyperledger.fabric.gateway.Contract;
import org.springframework.beans.factory.annotation.Autowired;
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
		// initFabricService();
		SpringApplication.run(TradingSystemApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	// @GetMapping("/fabric")
	// public void test(){
	// 	UserService service = new UserService();
	// 	service.testdatabase();
	// }


	private static void initFabricService() throws Exception{
		fService = new FabricService();
	}

	public static FabricService getFabricService(){
		return fService;
	}

}