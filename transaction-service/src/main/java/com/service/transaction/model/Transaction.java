package com.service.transaction.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_tbl")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountNumber;
    private long amount;
    private String transactionType; // CREDIT , DEBIT and TRANSFER
    private String status;          // SUCCESS or FAILURE
    private String message;
    @Column(name = "Total_Balance")
    private long totalBalance; // total Balance
}
