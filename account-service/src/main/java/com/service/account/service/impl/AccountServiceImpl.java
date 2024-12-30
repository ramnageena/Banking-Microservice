package com.service.account.service.impl;

import com.service.account.dto.AccountResponse;
import com.service.account.dto.CustomerResponse;
import com.service.account.exceptions.ResourceNotFoundException;
import com.service.account.model.Account;
import com.service.account.repository.AccountRepository;
import com.service.account.responseApi.ApiResponse;
import com.service.account.service.AccountService;
import com.service.account.utils.AccountUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;
    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public AccountServiceImpl(AccountRepository accountRepository, ModelMapper modelMapper, WebClient webClient) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
    }

    @Override
    public Account newAccount(Account account) {
        logger.info("Creating a new account for customerId: {}", account.getCustomerId());
        account.setAccountNumber(AccountUtil.generateAccountNumber());

        String customerUri = customerServiceUrl + account.getCustomerId();
        logger.debug("Customer service URI : {}", customerUri);

        CustomerResponse customerResponse = webClient.get()
                .uri(customerUri)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .block();

        if (customerResponse == null || customerResponse.getCustomerId() != account.getCustomerId()) {
            logger.error("Invalid customer details for customerId: {}", account.getCustomerId());
            throw new ResourceNotFoundException("Invalid customer details for customerId: " + account.getCustomerId());
        }

        Account savedAccount = accountRepository.save(account);
        logger.info("Account successfully created with accountId: {}", savedAccount.getAccountId());
        return savedAccount;
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        logger.info("Fetching all accounts.");
        List<Account> accounts = accountRepository.findAll();
        logger.info("Account size: {}",accounts.size());
        return accounts.stream().map(account -> modelMapper.map(account, AccountResponse.class)).toList();
    }

    @Override
    public AccountResponse getAccountById(long accountId) {
        logger.info("Fetching account details for accountId: {}", accountId);
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found with accountId: " + accountId));
        return modelMapper.map(account, AccountResponse.class);
    }

    @Override
    public AccountResponse updateBalance(long accountNumber, long amount) {
        logger.info("Updating balance for accountNumber: {} by amount: {}", accountNumber, amount);
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            logger.error("Account not found for accountNumber: {}", accountNumber);
            throw new ResourceNotFoundException("Account not found for accountNumber: " + accountNumber);
        }

        account.setBalance(account.getBalance() + amount);
        Account updatedAccount = accountRepository.save(account);
        logger.info("Balance updated successfully for accountNumber: {}", accountNumber);

        return modelMapper.map(updatedAccount, AccountResponse.class);
    }

    @Override
    public ApiResponse getCustomerDetailsByAccountNumber(long accountNumber) {
        logger.info("Fetching customer details for accountNumber: {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            logger.error("No account found for accountNumber: {}", accountNumber);
            throw new ResourceNotFoundException("Account not found for accountNumber: " + accountNumber);
        }

        String customerUri = customerServiceUrl + account.getCustomerId();
        logger.debug("Customer service URI: {}", customerUri);

        CustomerResponse customerResponse = webClient.get()
                .uri(customerUri)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .block();

        if (customerResponse == null) {
            logger.error("Customer details not found for customerId: {}", account.getCustomerId());
            throw new ResourceNotFoundException("Customer details not found for customerId: " + account.getCustomerId());
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setAccountResponse(modelMapper.map(account, AccountResponse.class));
        apiResponse.setCustomerResponse(customerResponse);

        logger.info("Customer details successfully fetched for accountNumber: {}", accountNumber);
        return apiResponse;
    }

    @Override
    public AccountResponse getAccountDetails(long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        return modelMapper.map(account,AccountResponse.class);
    }
}
