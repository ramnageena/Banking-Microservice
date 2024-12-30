package com.service.transaction.repository;

import com.service.transaction.dto.TransactionResponse;
import com.service.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction>findByAccountNumber(long accountNumber);
}
