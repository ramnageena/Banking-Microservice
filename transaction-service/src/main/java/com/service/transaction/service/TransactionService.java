package com.service.transaction.service;

import com.service.transaction.dto.TransactionHistory;
import com.service.transaction.dto.TransactionRequest;
import com.service.transaction.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {

    TransactionResponse processTransaction(TransactionRequest request);
    List<TransactionHistory> getAllTransactionHistory(long accountNumber);
}
