package com.example.transactions_api.models;

import lombok.Data;

@Data
public class Transaction {
    private final String type;
    private double amount;
    private Long parentId;


    public Transaction(final String type, final double amount){
        this.type = type;
        this.amount = amount;
        this.parentId = null;
    }
}
