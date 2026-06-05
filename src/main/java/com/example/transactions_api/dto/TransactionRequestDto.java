package com.example.transactions_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionRequestDto(
        Double amount,
        String type,
        @JsonProperty("parent_id") Long parentId
) {}
