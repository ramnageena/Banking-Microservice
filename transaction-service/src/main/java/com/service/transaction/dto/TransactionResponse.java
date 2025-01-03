package com.service.transaction.dto;

import lombok.Data;

@Data
public class TransactionResponse {
    private Long transactionId;
    private Long accountNumber;
    private long amount;
    private String transactionType; // CREDIT , DEBIT and TRANSFER
    private String status;          // SUCCESS or FAILURE
    private String message;
    private long totalBalance;
    private CustomerResponse customerResponse;
}
