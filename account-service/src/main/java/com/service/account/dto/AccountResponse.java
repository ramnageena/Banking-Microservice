package com.service.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private long accountId;
    private Long accountNumber;
    private String accountType;
    private long balance;
    private long customerId;
}
