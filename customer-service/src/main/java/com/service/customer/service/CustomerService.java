package com.service.customer.service;

import com.service.customer.dto.CustomerRequest;
import com.service.customer.dto.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse newCustomer(CustomerRequest customerRequest);
    List<CustomerResponse> getAllCustomer();
    CustomerResponse getCustomerById(long customerId);
    CustomerResponse updateCustomer(Long customerId,CustomerRequest customerRequest);
    void deleteCustomer(long customerId);

}
