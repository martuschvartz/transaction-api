package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTransactionDao implements TransactionDao {

    final Map<Long, Transaction> store;
    final Map<String, List<Long>> transactionByType;

    public InMemoryTransactionDao(){
        this.store = new ConcurrentHashMap<>();
        this.transactionByType = new ConcurrentHashMap<>();
    }

    @Override
    public Transaction createTransaction(long id, String type, double amount) {
        return createTransaction(id, type, amount, null);
    }

    @Override
    public Transaction createTransaction(long id, String type, double amount, Long parentId) {
        Transaction newTransaction = new Transaction(type, amount, parentId);
        store.put(id, newTransaction);
        transactionByType.computeIfAbsent(type, newType -> {
            List<Long> newList = new ArrayList<>();
            newList.add(id);
            return newList;
        });
        return newTransaction;
    }

    @Override
    public List<Long> getTransactionsByType(String type) {
        return new ArrayList<>(transactionByType.getOrDefault(type, List.of()));
    }
}
