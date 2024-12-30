package com.service.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AccountResponse {
    private long accountId;
    private Long accountNumber;
    private String accountType;
    private long balance;
}
