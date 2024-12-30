package com.service.transaction.service;

import com.service.transaction.dto.TransactionRequest;
import com.service.transaction.dto.TransactionResponse;

public interface TransactionService {

    TransactionResponse processTransaction(TransactionRequest request);
}
