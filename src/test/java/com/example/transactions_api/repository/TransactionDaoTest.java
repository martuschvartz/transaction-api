package com.example.transactions_api.repository;

import com.example.transactions_api.interfaces.TransactionDao;
import com.example.transactions_api.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class TransactionDaoTest {
    private final static double EPSILON = 0.0001;
    TransactionDao transactionDao;

    @BeforeEach
    void setUp(){
        transactionDao = new InMemoryTransactionDao();
    }

    @Test
    void test_CreateTransaction_Successfully(){
        long validId = 1L;
        String type = "transaction type";
        double amount = 10.0;

        Transaction newTransaction = transactionDao.createTransaction(validId, type, amount);

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
        transactionDao.createTransaction(id, type, amount);

        List<Long> recoveredTransactions = transactionDao.getTransactionsByType(type);

        assertFalse(recoveredTransactions.isEmpty());
        assertEquals(1, recoveredTransactions.size());
        assertEquals(id, recoveredTransactions.getFirst());
    }

    @Test
    void test_GetTransactionByType_MultipleSameType(){
        //Prepare
        String type = "cars";
        transactionDao.createTransaction(1L, type, 1.0);
        transactionDao.createTransaction(2L, type, 2.0);

        List<Long> recoveredTransactions = transactionDao.getTransactionsByType(type);

        assertEquals(2, recoveredTransactions.size());
        assertTrue(recoveredTransactions.containsAll(List.of(1L, 2L)));
    }

    @Test
    void test_GetTransactionByType_UnknownType_ReturnsEmpty(){
        List<Long> recoveredTransactions = transactionDao.getTransactionsByType("does-not-exist");

        assertNotNull(recoveredTransactions);
        assertTrue(recoveredTransactions.isEmpty());
    }

    @Test
    void test_GetAll_ReturnsEveryStoredTransaction(){
        //Prepare
        transactionDao.createTransaction(1L, "type", 1.0);
        transactionDao.createTransaction(2L, "type", 2.0, 1L);

        Map<Long, Transaction> all = transactionDao.getAll();

        assertEquals(2, all.size());
        assertEquals(1.0, all.get(1L).amount(), EPSILON);
        assertEquals(1L, all.get(2L).parentId());
    }
}
