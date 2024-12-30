package com.service.account.service;

import com.service.account.dto.AccountResponse;
import com.service.account.model.Account;
import com.service.account.responseApi.ApiResponse;

import java.util.List;

public interface AccountService {

    Account newAccount(Account accountRequest);

    List<AccountResponse> getAllAccounts();

    AccountResponse getAccountById(long accountId);

    AccountResponse updateBalance(long accountNumber, long amount);

    ApiResponse getCustomerDetailsByAccountNumber(long accountNumber);

    AccountResponse getAccountDetails(long accountNumber);


}
