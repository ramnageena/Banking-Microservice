package com.service.account.dto;

import lombok.Data;

@Data
public class UpdateBalance {
    private long accountNumber;
    private long amount;
}
