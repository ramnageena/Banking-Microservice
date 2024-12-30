package com.service.customer.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "Customer name cannot be blank")
    @Size(min = 2, max = 50, message = "Customer name must be between 2 and 50 characters")
    private String customerName;

    @NotBlank(message = "Customer email cannot be blank")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotNull(message = "Phone number cannot be null")
    @Digits(integer = 10, fraction = 0, message = "Phone number must be a 10-digit number")
    private Long phoneNumber;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}
