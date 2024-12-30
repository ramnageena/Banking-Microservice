package com.service.transaction.exception;

public class InvalidTransactionType extends RuntimeException {
   public InvalidTransactionType(String message){
       super(message);

    }
}
