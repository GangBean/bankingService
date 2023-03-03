package com.example.bankingservice.exception.trade;

public class WithdrawAccountNotExistsException extends TradeException {

    public WithdrawAccountNotExistsException(String message) {
        super(message);
    }
}
