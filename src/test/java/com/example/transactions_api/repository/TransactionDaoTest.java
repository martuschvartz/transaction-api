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

}
