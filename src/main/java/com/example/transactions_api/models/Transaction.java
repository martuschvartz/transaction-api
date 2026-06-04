package com.example.transactions_api.models;

public record Transaction(String type, double amount, Long parentId) {}
