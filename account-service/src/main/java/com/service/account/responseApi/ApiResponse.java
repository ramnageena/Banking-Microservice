package com.service.account.responseApi;

import com.service.account.dto.AccountResponse;
import com.service.account.dto.CustomerResponse;
import lombok.Data;

@Data
public class ApiResponse {
    private AccountResponse accountResponse;
    private CustomerResponse customerResponse;
}
