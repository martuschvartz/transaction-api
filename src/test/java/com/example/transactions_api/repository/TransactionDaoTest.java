package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TransactionDaoTest {
    private final static double EPSILON = 0.0001;
    TransactionDao ts;

    @BeforeEach
    void setUp(){
        ts = new InMemoryTransactionDao();
    }

    @Test
    void test_CreateTransaction_Successfully(){
        long validId = 1L;
        String type = "transaction type";
        double amount = 10.0;

        Transaction newTransaction = ts.createTransaction(validId, type, amount);

        assertNotNull(newTransaction);
        assertEquals(type, newTransaction.type());
        assertEquals(amount, newTransaction.amount(), EPSILON);
    }

    @Test
    void test_GetTransactionByType_Successfully(){
        //Prepare
        long id = 1L;
        String type = "my-type";
        double amount = 0.0;
        ts.createTransaction(id, type, amount);

        List<Long> recoveredTransactions = ts.getTransactionsByType(type);

        assertFalse(recoveredTransactions.isEmpty());
        assertEquals(1, recoveredTransactions.size());
        assertEquals(id, recoveredTransactions.getFirst());
    }

    @Test
    void test_GetTransactionSum_DirectConnection(){
        // Prepare
        double expected = 2.0;
        long parentId = 1L;
        ts.createTransaction(parentId, "type", 1.0);
        ts.createTransaction(2L, "type", 1.0, parentId);

        double sum = ts.getAmountSum(parentId);

        assertEquals(expected, sum, EPSILON);
    }

    @Test
    void test_GetTransactionSum_TransitiveConnection(){
        // Prepare
        double expected = 3.0;
        long grandpaId = 1L;
        long parentId = 2L;
        ts.createTransaction(grandpaId, "type", 1.0);
        ts.createTransaction(parentId, "type", 1.0, grandpaId);
        ts.createTransaction(3L, "type", 1.0, parentId);

        double sum = ts.getAmountSum(grandpaId);

        assertEquals(expected, sum, EPSILON);
    }
}
