package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransactionDao implements TransactionDao {

    private final Map<Long, Transaction> store;
    private final Map<String, Set<Long>> transactionByType;

    public InMemoryTransactionDao(){
        this.store = new ConcurrentHashMap<>();
        this.transactionByType = new ConcurrentHashMap<>();
    }

    @Override
    public Transaction createTransaction(long id, String type, double amount, Long parentId) {
        Transaction newTransaction = new Transaction(type, amount, parentId);
        store.put(id, newTransaction);
        transactionByType.computeIfAbsent(type, newType -> ConcurrentHashMap.newKeySet()).add(id);
        return newTransaction;
    }

    @Override
    public Set<Long> getTransactionsByType(String type) {
        if(type == null || type.isBlank())
            return Set.of();
        return Set.copyOf(transactionByType.getOrDefault(type, Set.of()));
    }

    @Override
    public Map<Long, Transaction> getAll() {
        return Map.copyOf(store);
    }

    @Override
    public Set<String> getExistingTypes() {
        return Set.copyOf(transactionByType.keySet());
    }
}
