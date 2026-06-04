package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;

import java.util.*;
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

    @Override
    public double getAmountSum(long transactionId) {
        double sum = 0.0;
        Deque<Long> toExplore = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        toExplore.add(transactionId);

        while (!toExplore.isEmpty()) {
            Long currentId = toExplore.poll();

            Transaction current = store.get(currentId);
            if (current == null) continue;

            if (!visited.add(currentId)) continue;
            sum += current.amount();

            // Add children to frontier
            for (Map.Entry<Long, Transaction> entry : store.entrySet()) {
                Long pid = entry.getValue().parentId();
                if (pid != null && pid.equals(currentId)) {
                    toExplore.add(entry.getKey());
                }
            }
        }
        return sum;
    }
}
