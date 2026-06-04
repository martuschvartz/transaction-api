package com.example.transactions_api.interfaces;

import com.example.transactions_api.models.Transaction;

public interface TransactionDao {
    Transaction createTransaction(long validId, String type, double amount);
}
