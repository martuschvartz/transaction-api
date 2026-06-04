package com.example.transactions_api.services;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.interfaces.TransactionService;
import com.example.transactions_api.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionDao transactionDao;

    @Autowired
    public TransactionServiceImpl(final TransactionDao transactionDao){
        this.transactionDao=transactionDao;
    }

    @Override
    public Transaction createTransaction(long id, String type, double amount, Long parentId) {
        return transactionDao.createTransaction(id, type, amount, parentId);
    }

    @Override
    public List<Long> getTransactionsByType(String type) {
        if(type == null || type.isBlank())
            return List.of();
        return transactionDao.getTransactionsByType(type);
    }

    @Override
    public double getAmountSum(long id) {
        Map<Long, Transaction> all = transactionDao.getAll();

        double sum = 0.0;
        Deque<Long> toExplore = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        toExplore.add(id);

        while (!toExplore.isEmpty()) {
            Long currentId = toExplore.poll();

            Transaction current = all.get(currentId);
            if (current == null) continue;

            if (!visited.add(currentId)) continue;
            sum += current.amount();

            // Add children to frontier
            for (Map.Entry<Long, Transaction> entry : all.entrySet()) {
                Long pid = entry.getValue().parentId();
                if (pid != null && pid.equals(currentId)) {
                    toExplore.add(entry.getKey());
                }
            }
        }
        return sum;
    }
}
