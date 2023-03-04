package com.example.bankingservice.exception.member;

import com.example.bankingservice.exception.ApplicationException;

public class MemberException extends ApplicationException {

    public MemberException() {
    }

    public MemberException(String message) {
        super(message);
    }
}
