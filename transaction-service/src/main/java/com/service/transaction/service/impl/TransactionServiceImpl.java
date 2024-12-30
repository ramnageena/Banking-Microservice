package com.service.transaction.service.impl;

import com.service.transaction.dto.*;
import com.service.transaction.exception.AccountNotFoundException;
import com.service.transaction.exception.InvalidTransactionType;
import com.service.transaction.model.Transaction;
import com.service.transaction.repository.TransactionRepository;
import com.service.transaction.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import static com.service.transaction.constant.TransactionConstant.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Value("${account.service.url}")
    private String accountServiceUrl;
    @Value("${update.account.service.url}")
    private String updateAccountServiceUrl;

    private final TransactionRepository repository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;

    public TransactionServiceImpl(TransactionRepository repository, ModelMapper modelMapper,WebClient webClient) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.webClient = webClient;
    }


   @Override
   @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        if (DEPOSIT.equalsIgnoreCase(request.getTransactionType())) {
            return processDeposit(request);
        } else if (WITHDRAW.equalsIgnoreCase(request.getTransactionType())) {
            return processWithdraw(request);
        } else if (TRANSFER.equalsIgnoreCase(request.getTransactionType())) {
            return processTransfer(request);
        } else {
            throw new InvalidTransactionType("Invalid transaction type");
        }
    }

    private TransactionResponse processDeposit(TransactionRequest request) {
        AccountResponse accountResponse = updateAccountBalance(request.getAccountNumber(), request.getAmount());
        return saveTransaction(request, SUCCESS, DEPOSIT_SUCCESSFUL,accountResponse.getBalance());
    }

    private TransactionResponse processWithdraw(TransactionRequest request) {
        AccountResponse account = validateAccount(request.getAccountNumber());
        if (account.getBalance() < request.getAmount()) {
            return saveTransaction(request, FAILURE, INSUFFICIENT_BALANCE, account.getBalance());
         }
        AccountResponse accountResponse = updateAccountBalance(request.getAccountNumber(), - request.getAmount());
        return saveTransaction(request, SUCCESS, WITHDRAW_SUCCESSFUL, accountResponse.getBalance());
    }

    private TransactionResponse processTransfer(TransactionRequest request) {
        AccountResponse sourceAccount = validateAccount(request.getAccountNumber());
        AccountResponse targetAccount = validateAccount(request.getTargetAccountNumber());

        if (sourceAccount.getBalance() < request.getAmount()) {
            return saveTransaction(request, FAILURE, "Insufficient balance for transfer", sourceAccount.getBalance());
        }

        updateAccountBalance(request.getAccountNumber(), - request.getAmount());
        updateAccountBalance(request.getTargetAccountNumber(), request.getAmount());
        return saveTransaction(request, SUCCESS, TRANSFER_SUCCESSFUL, targetAccount.getBalance());
    }

    private AccountResponse validateAccount(Long accountNumber) {
        try {
            return webClient.get()
                    .uri("http://localhost:8082/api/v1/accountDetails/" + accountNumber)
                    .retrieve()
                    .bodyToMono(AccountResponse.class)
                    .block();
        } catch (Exception e) {
            throw new AccountNotFoundException("Account validation failed");
        }
    }

    private AccountResponse updateAccountBalance(Long accountNumber, long amount) {

        UpdateBalance updateBalance= new UpdateBalance(accountNumber,amount);
        return webClient.put()
                .uri("http://localhost:8082/api/v1/update-balance")
                .bodyValue(updateBalance)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .block();

    }

    private TransactionResponse saveTransaction(TransactionRequest request, String status, String message, long balance) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setStatus(status);
        transaction.setMessage(message);
        transaction.setTotalBalance(balance);
        return modelMapper.map(repository.save(transaction), TransactionResponse.class);
    }

}
