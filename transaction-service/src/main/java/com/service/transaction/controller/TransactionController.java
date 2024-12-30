package com.service.transaction.controller;

import com.service.transaction.dto.TransactionRequest;
import com.service.transaction.dto.TransactionResponse;
import com.service.transaction.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }



    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> processTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.processTransaction(request));
    }

}
