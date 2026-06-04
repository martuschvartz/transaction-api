package com.example.transactions_api.interfaces;

import com.example.transactions_api.models.Transaction;

import java.util.List;

public interface TransactionDao {
    Transaction createTransaction(long validId, String type, double amount);
    Transaction createTransaction(long validId, String type, double amount, Long parentId);

    List<Long> getTransactionsByType(String type);
}
