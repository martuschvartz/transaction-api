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
        long id = 1L;
        String type = "transaction-type";
        double amount = 10.0;

        Transaction newTransaction = transactionDao.createTransaction(id, type, amount, null);

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
        transactionDao.createTransaction(id, type, amount, null);

        List<Long> recoveredTransactions = transactionDao.getTransactionsByType(type);

        assertFalse(recoveredTransactions.isEmpty());
        assertEquals(1, recoveredTransactions.size());
        assertEquals(id, recoveredTransactions.getFirst());
    }

    @Test
    void test_GetTransactionByType_MultipleSameType(){
        //Prepare
        String type = "cars";
        transactionDao.createTransaction(1L, type, 1.0, null);
        transactionDao.createTransaction(2L, type, 2.0, null);

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
        long id1 = 1L;
        long id2 = 2L;
        String type = "type";
        double amount1 = 1.0;
        double amount2 = 2.0;

        transactionDao.createTransaction(id1, type, amount1, null);
        transactionDao.createTransaction(id2, type, amount2, id1);

        Map<Long, Transaction> all = transactionDao.getAll();

        assertEquals(2, all.size());

        assertTrue(all.containsKey(id1));
        assertEquals(type, all.get(id1).type());
        assertEquals(amount1, all.get(id1).amount(), EPSILON);
        assertNull(all.get(id1).parentId());

        assertTrue(all.containsKey(id2));
        assertEquals(type, all.get(id2).type());
        assertEquals(amount2, all.get(id2).amount(), EPSILON);
        assertEquals(id1, all.get(id2).parentId());
    }
}
