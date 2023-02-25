package com.example.bankingservice.service;

import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.repository.AccountRepository;
import com.example.bankingservice.domain.view.dto.AccountDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountDto openAccount(AccountDto accountDto) {
        return AccountDto.accountOf(accountRepository.save(accountDto.asAccount()));
    }

    public AccountDto readAccounts(AccountDto accountDto) {
        return AccountDto.accountOf(
            accountRepository.findByMemberId(
                Optional.ofNullable(accountDto.getMember())
                    .orElse(Member.builder().build())
                    .getId())
                .get());
    }
}
