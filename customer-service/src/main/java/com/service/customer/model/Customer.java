package com.service.customer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "customer_tbl")
public class Customer {
    @Id
    private long customerId;
    @Column(name = "Account_Holder_Name")
    private String customerName;
    private String customerEmail;
    private Long phoneNumber;
    private String address;
}
