package com.example.bankingservice.exception.trade;

public class DepositAccountNotExistsException extends TradeException {

    public DepositAccountNotExistsException(String message) {
        super(message);
    }
}
