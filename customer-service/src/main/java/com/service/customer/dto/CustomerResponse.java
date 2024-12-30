package com.service.customer.dto;

import lombok.Data;

@Data
public class CustomerResponse {
    private long customerId;
    private String customerName;
    private String customerEmail;
    private Long phoneNumber;
    private String address;
}
