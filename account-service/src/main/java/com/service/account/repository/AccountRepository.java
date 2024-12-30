package com.service.account.repository;

import com.service.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account ,Long> {

    Account findByAccountNumber(long accountNumber);
}
