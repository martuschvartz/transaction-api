package com.example.transactions_api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test_PutTransaction_ReturnsOkStatus() throws Exception {
        mockMvc.perform(put("/transactions/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":5000,\"type\":\"cars\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void test_GetByType_ReturnsTransactionIds() throws Exception {
        mockMvc.perform(put("/transactions/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":5000,\"type\":\"bikes\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/transactions/types/bikes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(20));
    }

    @Test
    void test_GetByType_UnknownType_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/transactions/types/does-not-exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void test_GetSum_ReturnsTransitiveSum() throws Exception {
        // 31 child of 30, 32 child of 31  ->  sum(30) = 5000 + 10000 + 5000
        mockMvc.perform(put("/transactions/30")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":5000,\"type\":\"cars\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/transactions/31")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":10000,\"type\":\"shopping\",\"parent_id\":30}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/transactions/32")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":5000,\"type\":\"shopping\",\"parent_id\":31}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/transactions/sum/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(20000.0));
    }
}
