package com.example.transactions_api.controllers;

import com.example.transactions_api.dto.StatusResponse;
import com.example.transactions_api.dto.SumResponse;
import com.example.transactions_api.interfaces.TransactionService;
import com.example.transactions_api.dto.TransactionRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(final TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusResponse> createTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequestDto transactionRequestDto){
        transactionService.createTransaction(
                id,
                transactionRequestDto.type(),
                transactionRequestDto.amount(),
                transactionRequestDto.parentId()
        );
        return ResponseEntity.ok(new StatusResponse("ok"));
    }

    @GetMapping("/types")
    public ResponseEntity<Set<String>> getExistingTypes(){
        Set<String> types = transactionService.getExistingTypes();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/types/{type}")
    public ResponseEntity<Set<Long>> getTransactionsByType(
            @PathVariable String type
    ){
        Set<Long> ids = transactionService.getTransactionsByType(type);
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/sum/{parentId}")
    public ResponseEntity<SumResponse> getAmountSum(
            @PathVariable Long parentId
    ){
        double sum = transactionService.getAmountSum(parentId);
        return ResponseEntity.ok(new SumResponse(sum));
    }
}
