package com.service.customer.utils;

public class CustomerUtils {
    private CustomerUtils() {

    }

    public static long customerIdGenerate() {
        return   (int) (Math.random() * 90000) + 10000;
    }

}
