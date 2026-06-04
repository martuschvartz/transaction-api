package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTransactionDao implements TransactionDao {
    @Override
    public Transaction createTransaction(long validId, String type, double amount) {
        return null;
    }
}
