package com.service.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    private String accountType;
    private long balance;
    private long customerId;
}
