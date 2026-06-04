package com.example.transactions_api.services;

import com.example.transactions_api.interfaces.TransactionService;
import com.example.transactions_api.repository.InMemoryTransactionDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TransactionServiceTest {
    private final static double EPSILON = 0.0001;
    TransactionService transactionService;

    @BeforeEach
    void setUp(){
        transactionService = new TransactionServiceImpl(new InMemoryTransactionDao());
    }

    @Test
    void test_GetAmountSum_DirectConnection(){
        // Prepare: direct connection
        double expected = 2.0;
        long parentId = 1L;
        transactionService.createTransaction(parentId, "type", 1.0);
        transactionService.createTransaction(2L, "type", 1.0, parentId);

        double sum = transactionService.getAmountSum(parentId);

        assertEquals(expected, sum, EPSILON);
    }

    @Test
    void test_GetAmountSum_TransitiveConnection(){
        // Prepare: 3 is child of 2, and 2 is children of 1
        double expected = 3.0;
        long grandpaId = 1L;
        long parentId = 2L;
        transactionService.createTransaction(grandpaId, "type", 1.0);
        transactionService.createTransaction(parentId, "type", 1.0, grandpaId);
        transactionService.createTransaction(3L, "type", 1.0, parentId);

        double sum = transactionService.getAmountSum(grandpaId);

        assertEquals(expected, sum, EPSILON);
    }

    @Test
    void test_GetAmountSum_OnlyCountsDescendants(){
        // Prepare: 2 is a child of 1, but 3 is unrelated
        long rootId = 1L;
        transactionService.createTransaction(rootId, "type", 1.0);
        transactionService.createTransaction(2L, "type", 1.0, rootId);
        transactionService.createTransaction(3L, "type", 100.0);

        double sum = transactionService.getAmountSum(rootId);

        assertEquals(2.0, sum, EPSILON);
    }

    @Test
    void test_GetAmountSum_UnknownId_ReturnsZero(){
        double sum = transactionService.getAmountSum(999L);

        assertEquals(0.0, sum, EPSILON);
    }
}
