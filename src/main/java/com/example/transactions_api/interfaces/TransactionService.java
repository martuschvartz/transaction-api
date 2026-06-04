package com.example.transactions_api.interfaces;

import com.example.transactions_api.models.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(long id, String type, double amount);
    Transaction createTransaction(long id, String type, double amount, long parentId);

    List<Long> getTransactionsByType(String type);

    double getAmountSum(long id);
}
