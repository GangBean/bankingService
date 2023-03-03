package com.example.bankingservice.exception.trade;

public class NotEnoughAmountException extends TradeException {

    public NotEnoughAmountException(String message) {
        super(message);
    }
}
