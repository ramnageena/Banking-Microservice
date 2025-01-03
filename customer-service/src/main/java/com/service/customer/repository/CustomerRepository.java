package com.service.customer.repository;

import com.service.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    boolean existsByCustomerEmail(String email);
}
