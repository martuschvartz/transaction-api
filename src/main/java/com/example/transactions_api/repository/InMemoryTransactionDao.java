package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransactionDao implements TransactionDao {

    private final Map<Long, Transaction> store;
    private final Map<String, List<Long>> transactionByType;

    public InMemoryTransactionDao(){
        this.store = new ConcurrentHashMap<>();
        this.transactionByType = new ConcurrentHashMap<>();
    }

    @Override
    public Transaction createTransaction(long id, String type, double amount, Long parentId) {
        Transaction newTransaction = new Transaction(type, amount, parentId);
        store.put(id, newTransaction);
        transactionByType.computeIfAbsent(type, newType -> new ArrayList<>()).add(id);
        return newTransaction;
    }

    @Override
    public List<Long> getTransactionsByType(String type) {
        if(type == null || type.isBlank())
            return List.of();
        return List.copyOf(transactionByType.getOrDefault(type, List.of()));
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
