package com.service.customer.controlller;

import com.service.customer.dto.CustomerRequest;
import com.service.customer.dto.CustomerResponse;
import com.service.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/create-customers")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid  @RequestBody CustomerRequest customerRequest) {
        logger.info("Received request to create a new customer: {}", customerRequest);
        CustomerResponse customerResponse = customerService.newCustomer(customerRequest);
        return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        logger.info("Received request to fetch all customers");
        List<CustomerResponse> customers = customerService.getAllCustomer();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/getCustomerById/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable("id") long customerId) {
        logger.info("Received request to fetch customer with ID: {}", customerId);
        CustomerResponse customerResponse = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customerResponse);
    }

    @PutMapping("/updateCustomer/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable("id") long customerId,
            @Valid @RequestBody CustomerRequest customerRequest) {
        logger.info("Received request to update customer with ID: {}", customerId);
        CustomerResponse updatedCustomer = customerService.updateCustomer(customerId, customerRequest);
        return ResponseEntity.ok(updatedCustomer);

    }

    @DeleteMapping("deleteCustomerById/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") long customerId) {
        logger.info("Received request to delete customer with ID: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Customer with ID " + customerId + " deleted successfully.");
    }
}
