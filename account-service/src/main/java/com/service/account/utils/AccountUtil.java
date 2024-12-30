package com.service.account.utils;

import java.util.Random;

public class AccountUtil {
    private AccountUtil() {

    }
    public static Long generateAccountNumber() {
        Random random = new Random();
        return 1_000_000_0000L + (long) (random.nextDouble() * 9_000_000_0000L);
    }

}
