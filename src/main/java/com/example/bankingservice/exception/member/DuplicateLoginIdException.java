package com.example.bankingservice.exception.member;

public class DuplicateLoginIdException extends MemberException {

    public DuplicateLoginIdException(String message) {
        super(message);
    }
}
