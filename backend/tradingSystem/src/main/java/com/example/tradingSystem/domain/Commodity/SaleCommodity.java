package com.example.tradingSystem.domain.Commodity;

import lombok.Data;

@Data
public class SaleCommodity {

    private String name;

    private String price;

    // issuer account
    private String issuerId;

    private String issuerName;
    
}
