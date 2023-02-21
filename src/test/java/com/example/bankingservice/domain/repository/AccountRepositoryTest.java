package com.example.bankingservice.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.member.Member;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MemberRepository memberRepository;

    Logger log = LoggerFactory.getLogger(AccountRepositoryTest.class);

    @Test
    @DisplayName("계좌 생성 정상 테스트")
    void createAccount() {
        // given
        String userName = "이름";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .build();

        // when
        Account savedAccount = accountRepository.save(account);

        // then
        log.info(savedAccount.toString());
        assertThat(savedAccount.getMember()).isSameAs(savedMember);
        // assertThat(savedAccount.getAmount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("계좌 조회 정상 테스트")
    void readAccount() {
        // given
        String userName = "이름";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .build();

        Account savedAccount = accountRepository.save(account);

        // when
        Account readAccount = accountRepository.findById(savedAccount.getId()).get();

        // then
        log.info(readAccount.toString());
        assertThat(readAccount).isSameAs(account);
    }

    @Test
    @DisplayName("계좌 생성 비정상 테스트-계좌 잔액 마이너스")
    void createAccountWithMinusAmount() {
        // given
        String userName = "이름";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .amount(-1000L)
            .build();

        // when
        Assertions.assertThrows(ConstraintViolationException.class,
            () -> accountRepository.save(account));
    }

    @Test
    @DisplayName("계좌 생성 비정상 테스트-미존재 회원")
    void createAccountWithUnsavedMember() {
        // given
        String userName = "이름";
        String loginId = "아이디";
        Member member = Member.builder()
            .id(1L)
            .userName(userName)
            .loginId(loginId)
            .build();

        Account account = Account.builder()
            .member(member)
            .amount(1000L)
            .build();

        // when
        Assertions.assertThrows(DataIntegrityViolationException.class,
            () -> accountRepository.save(account));
    }

}