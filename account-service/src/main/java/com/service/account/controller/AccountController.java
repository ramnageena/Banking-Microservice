package com.service.account.controller;

import com.service.account.dto.AccountResponse;
import com.service.account.dto.UpdateBalance;
import com.service.account.model.Account;
import com.service.account.responseApi.ApiResponse;
import com.service.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @PostMapping("/createAccount")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account accountRequest) {
        Account accountResponse = accountService.newAccount(accountRequest);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }

    @GetMapping("/getAllAccounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/getAccountById/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("id") long accountId) {
        AccountResponse accountResponse = accountService.getAccountById(accountId);
        return ResponseEntity.ok(accountResponse);
    }

    @GetMapping("/customer-details/{accountNumber}")
    public ResponseEntity<ApiResponse> getCustomerDetailsByAccountNumber(@PathVariable("accountNumber") long accountNumber) {
        ApiResponse apiResponse = accountService.getCustomerDetailsByAccountNumber(accountNumber);
        return ResponseEntity.ok(apiResponse);
    }
    @GetMapping("/accountDetails/{accountNumber}")
    public ResponseEntity<AccountResponse> getCustomerDetails(@PathVariable("accountNumber") long accountNumber) {
        AccountResponse apiResponse = accountService.getAccountDetails(accountNumber);
        return ResponseEntity.ok(apiResponse);
    }


    @PutMapping("/update-balance")
    public ResponseEntity<AccountResponse> updateBalance(@RequestBody UpdateBalance updateRequest) {
        AccountResponse accountResponse = accountService.updateBalance(updateRequest.getAccountNumber(), updateRequest.getAmount());
        return new ResponseEntity<>(accountResponse,HttpStatus.OK);
    }
}
