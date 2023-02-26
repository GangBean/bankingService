package com.example.bankingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.repository.AccountRepository;
import com.example.bankingservice.view.dto.AccountDto;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountService accountService;

    @Test
    @DisplayName("계좌 개설 정상 테스트")
    void openAccount() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .id(1L)
            .userName(userName)
            .loginId(loginId)
            .build();

        Account account1 = Account.builder()
            .id(1L)
            .member(member)
            .amount(1000L)
            .build();

        // when
        when(accountRepository.save(any())).thenReturn(account1);
        AccountDto accountDto = accountService.openAccount(AccountDto.accountOf(account1));

        // then
        assertThat(accountDto.getMember()).isSameAs(member);
    }

    @Test
    @DisplayName("계좌 조회 정상 테스트")
    void readAccount() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .id(1L)
            .userName(userName)
            .loginId(loginId)
            .build();

        Account account1 = Account.builder()
            .id(1L)
            .member(member)
            .amount(0L)
            .build();

        // when
        when(accountRepository.findByMemberId(any())).thenReturn(Optional.ofNullable(account1));
        AccountDto accountDto = accountService.readAccounts(AccountDto.accountOf(account1));

        // then
        assertThat(accountDto.getMember()).isSameAs(member);
        assertThat(accountDto.getAmount()).isEqualTo(0L);
    }
}
