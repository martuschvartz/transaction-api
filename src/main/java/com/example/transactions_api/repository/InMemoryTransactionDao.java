package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTransactionDao implements TransactionDao {

    final Map<Long, Transaction> store;

    public InMemoryTransactionDao(){
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public Transaction createTransaction(long id, String type, double amount) {
        Transaction newTransaction = new Transaction(type, amount);
        store.put(id, newTransaction);
        return newTransaction;
    }
}
