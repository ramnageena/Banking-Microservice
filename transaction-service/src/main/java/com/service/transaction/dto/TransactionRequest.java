package com.service.transaction.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long accountNumber;// "accountNumber": 41431273058, "targetAccountNumber":24617360341
    private long amount;
    private String transactionType; // CREDIT or DEBIT
    private Long targetAccountNumber; // transfer
}
