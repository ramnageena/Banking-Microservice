package com.service.customer.service.impl;

import com.service.customer.dto.CustomerRequest;
import com.service.customer.dto.CustomerResponse;
import com.service.customer.exceptions.CustomerAlreadyExistsException;
import com.service.customer.exceptions.ResourceNotFoundException;
import com.service.customer.model.Customer;
import com.service.customer.repository.CustomerRepository;
import com.service.customer.service.CustomerService;
import com.service.customer.utils.CustomerUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerResponse newCustomer(CustomerRequest customerRequest) throws CustomerAlreadyExistsException {
        try {
            if (customerRepository.existsByCustomerEmail(customerRequest.getCustomerEmail())) {
                logger.warn("Customer already exists with email: {}", customerRequest.getCustomerEmail());
                throw new CustomerAlreadyExistsException("Customer already exists with email: " + customerRequest.getCustomerEmail());
            }

            Customer customer = modelMapper.map(customerRequest, Customer.class);
            customer.setCustomerId(CustomerUtils.customerIdGenerate());
            Customer savedCustomer = customerRepository.save(customer);
            logger.info("New customer created successfully with ID: {}", savedCustomer.getCustomerId());

            return modelMapper.map(savedCustomer, CustomerResponse.class);

        } catch (Exception e) {
            logger.error("Unexpected error occurred while creating customer: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<CustomerResponse> getAllCustomer() {
        try {
            List<Customer> customers = customerRepository.findAll();
            logger.info("Fetched all customers. Total count: {}", customers.size());
            return customers.stream().map(customer -> modelMapper.map(customer, CustomerResponse.class)).toList();
        } catch (Exception e) {
            logger.error("Error fetching all customers: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching customers", e);
        }
    }

    @Override
    public CustomerResponse getCustomerById(long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found with customer ID: " + customerId));
            logger.info("Successfully fetched customer with ID: {}", customerId);
            return modelMapper.map(customer, CustomerResponse.class);

        } catch (ResourceNotFoundException e) {
            logger.error("Customer not found with ID: {}", customerId);
            throw e; // Rethrow the exception after logging
        }
    }

    @Override
    public CustomerResponse updateCustomer(Long customerId, CustomerRequest customerRequest) {
        try {
            Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

            modelMapper.map(customerRequest, customer); // Update the customer object
            Customer updatedCustomer = customerRepository.save(customer);
            logger.info("Successfully updated customer with ID: {}", customerId);

            return modelMapper.map(updatedCustomer, CustomerResponse.class);

        } catch (ResourceNotFoundException e) {
            logger.error("Customer not found with ID :  {}", customerId);
            throw e; // Rethrow the exception after logging
        }
    }

    @Override
    public void deleteCustomer(long customerId) {
        try {
            if (!customerRepository.existsById(customerId)) {
                logger.warn("Attempted to delete non-existing customer with ID: {}", customerId);
                throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
            }
            customerRepository.deleteById(customerId);
            logger.info("Successfully deleted customer with ID: {}", customerId);

        } catch (ResourceNotFoundException e) {
            logger.error("Customer not found with customer ID : {}", customerId);
            throw e; // Rethrow the exception after logging
        }
    }
}
