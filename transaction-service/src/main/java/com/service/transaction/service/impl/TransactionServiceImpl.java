package com.service.transaction.service.impl;

import com.service.transaction.dto.*;
import com.service.transaction.exception.AccountNotFoundException;
import com.service.transaction.exception.InvalidTransactionType;
import com.service.transaction.model.Transaction;
import com.service.transaction.repository.TransactionRepository;
import com.service.transaction.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.service.transaction.constant.TransactionConstant.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Value("${account.service.url}")
    private String accountServiceUrl;
    @Value("${customer.service.url}")
    private String customerServiceUrl;

    private final TransactionRepository repository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;

    public TransactionServiceImpl(TransactionRepository repository, ModelMapper modelMapper, WebClient webClient) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
    }

    @Override
    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        log.info("Processing transaction of type: {}", request.getTransactionType());
        try {
            if (DEPOSIT.equalsIgnoreCase(request.getTransactionType())) {
                return processDeposit(request);
            } else if (WITHDRAW.equalsIgnoreCase(request.getTransactionType())) {
                return processWithdraw(request);
            } else if (TRANSFER.equalsIgnoreCase(request.getTransactionType())) {
                return processTransfer(request);
            } else {
                log.error("Invalid transaction type: {}", request.getTransactionType());
                throw new InvalidTransactionType("Invalid transaction type");
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the transaction: {}", e.getMessage(), e);
            throw e;
        }
    }

    private TransactionResponse processDeposit(TransactionRequest request) {
        log.info("Processing deposit for account: {}", request.getAccountNumber());
         //validating request
        validateRequest(request);
        AccountResponse accountResponse = updateAccountBalance(request.getAccountNumber(), request.getAmount());

        CustomerResponse customerDetails = getCustomerDetails(accountResponse.getCustomerId());
        return saveTransaction(request, SUCCESS, DEPOSIT_SUCCESSFUL,accountResponse.getBalance(),customerDetails);
    }

    private CustomerResponse getCustomerDetails(long customerId) {
        String uri = customerServiceUrl + CUSTOMER_DETAILS + customerId;
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .block();

    }

    private TransactionResponse processWithdraw(TransactionRequest request) {
        log.info("Processing withdrawal for account: {}", request.getAccountNumber());

        //validating request
        validateRequest(request);

         // Validate account
        AccountResponse account = validateAccount(request.getAccountNumber());
        if (account == null) {
            log.error("Account validation failed for account number: {}", request.getAccountNumber());
            throw new AccountNotFoundException("Account not found: " + request.getAccountNumber());
        }
        CustomerResponse customerDetails = getCustomerDetails(account.getCustomerId());
        if (account.getBalance() < request.getAmount()) {
            log.warn("Insufficient balance for account: {}. Current balance: {}, Requested amount: {}",
                    request.getAccountNumber(), account.getBalance(), request.getAmount());
            return saveTransaction(request, FAILURE, INSUFFICIENT_BALANCE, account.getBalance(), customerDetails);
         }
        // Update balance
        AccountResponse accountResponse = updateAccountBalance(request.getAccountNumber(), - request.getAmount());
        if (accountResponse == null ) {
            log.error("Failed to update account balance for account number: {}", request.getAccountNumber());
            throw new IllegalStateException("Failed to update account balance");
        }
        return saveTransaction(request, SUCCESS, WITHDRAW_SUCCESSFUL, accountResponse.getBalance(), customerDetails);
    }

    private TransactionResponse processTransfer(TransactionRequest request) {
        log.info("Processing transfer from account: {} to account: {}", request.getAccountNumber(), request.getTargetAccountNumber());
        //validating request
        validateRequest(request);
       try {
           //validating fromAccount
           AccountResponse sourceAccount = validateAccount(request.getAccountNumber());
           if (sourceAccount == null) {
               log.error("Source account validation failed for account number: {}", request.getAccountNumber());
               throw new AccountNotFoundException("Source account not found: " + request.getAccountNumber());
           }
           //validating toAccount
           AccountResponse targetAccount = validateAccount(request.getTargetAccountNumber());
           if (targetAccount == null) {
               log.error("Target account validation failed for account number: {}", request.getTargetAccountNumber());
               throw new AccountNotFoundException("Target account not found: " + request.getTargetAccountNumber());
           }

           CustomerResponse customerDetails = getCustomerDetails(sourceAccount.getCustomerId());
           if (sourceAccount.getBalance() < request.getAmount()) {
               log.warn("Insufficient balance for transfer from account: {}. Current balance: {}, Requested amount: {}",
                       request.getAccountNumber(), sourceAccount.getBalance(), request.getAmount());
               return saveTransaction(request, FAILURE, "Insufficient balance for transfer", sourceAccount.getBalance(), customerDetails);
           }

           updateAccountBalance(request.getAccountNumber(), - request.getAmount());
           updateAccountBalance(request.getTargetAccountNumber(), request.getAmount());
           return saveTransaction(request, SUCCESS, TRANSFER_SUCCESSFUL, sourceAccount.getBalance(), customerDetails);
       }catch (Exception e) {
           log.error("Error occurred while processing the transaction: {}", e.getMessage(), e);
           throw e;
       }
    }

    private AccountResponse validateAccount(Long accountNumber) {
        String uri = accountServiceUrl + ACCOUNT_DETAILS + accountNumber;
        log.info("Validating account with URI: {}", uri);
        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(AccountResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to validate account: {}", accountNumber, e);
            throw new AccountNotFoundException("Account validation failed");
        }
    }

    private AccountResponse updateAccountBalance(Long accountNumber, long amount) {
        String uri = accountServiceUrl + UPDATE_BALANCE;
        log.info("Updating balance for account: {} with URI: {}", accountNumber, uri);
        UpdateBalance updateBalance = new UpdateBalance(accountNumber, amount);
        try {
            return webClient.put()
                    .uri(uri)
                    .bodyValue(updateBalance)
                    .retrieve()
                    .bodyToMono(AccountResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Failed to update balance for account: {}", accountNumber, e);
            throw new AccountNotFoundException("Account Updating failed");
        }
    }

    private TransactionResponse saveTransaction(TransactionRequest request, String status, String message, long balance, CustomerResponse customerDetails) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setStatus(status);
        transaction.setMessage(message);
        transaction.setTotalBalance(balance);
        Transaction savedTransaction = repository.save(transaction);
        TransactionResponse transactionResponse = modelMapper.map(savedTransaction, TransactionResponse.class);
        transactionResponse.setCustomerResponse(customerDetails);
        return transactionResponse;
    }

    @Override
    public List<TransactionHistory> getAllTransactionHistory(long accountNumber) {
        log.info("Fetching transaction history for account: {}", accountNumber);
        try {
            List<Transaction> transactions = repository.findByAccountNumber(accountNumber);
            if (transactions.isEmpty()) {
                log.warn("No transactions found for account: {}", accountNumber);
                throw new AccountNotFoundException("No transactions found for account number: " + accountNumber);
            }
            return transactions.stream()
                    .map(transaction -> modelMapper.map(transaction, TransactionHistory.class))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch transaction history for account: {}", accountNumber, e);
            throw e;
        }
    }
    private void validateRequest(TransactionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("TransactionRequest cannot be null");
        }
        if (request.getAccountNumber() == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
    }
}
