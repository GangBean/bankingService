package com.example.bankingservice.service;

import com.example.bankingservice.domain.repository.AccountRepository;
import com.example.bankingservice.domain.view.dto.AccountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountDto openAccount(AccountDto accountDto) {
        return AccountDto.accountOf(accountRepository.save(accountDto.asAccount()));
    }

    public AccountDto readAccount(AccountDto accountDto) {
        return AccountDto.accountOf(
            accountRepository.findByMemberId(accountDto.getMember().getId()).get());
    }
}
