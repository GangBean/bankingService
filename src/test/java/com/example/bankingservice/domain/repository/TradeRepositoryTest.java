package com.example.bankingservice.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.account.Trade;
import com.example.bankingservice.domain.entity.account.Trade.TradeType;
import com.example.bankingservice.domain.entity.member.Member;
import java.time.LocalDateTime;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

@DataJpaTest
class TradeRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TradeRepository tradeRepository;

    @Test
    @DisplayName("거래 생성 정상 테스트")
    void createTrade() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .amount(1000L)
            .build();
        Account savedAccount = accountRepository.save(account);

        LocalDateTime now = LocalDateTime.now();
        Trade trade = Trade.builder()
            .account(savedAccount)
            .tradeDateTime(now)
            .tradeAmount(100L)
            .tradeType(TradeType.DEPOSIT)
            .build();

        // when
        Trade savedTrade = tradeRepository.save(trade);

        // then
        assertThat(savedTrade.getAccount()).isSameAs(savedAccount);
        assertThat(savedTrade.getTradeAmount()).isEqualTo(100L);
        assertThat(savedTrade.getTradeType()).isEqualTo(TradeType.DEPOSIT);
        assertThat(savedTrade.getTradeDateTime()).isEqualTo(now);
    }

    @Test
    @DisplayName("거래 생성 비정상 테스트 - 미존재계좌")
    void createTradeWithUnsavedAccount() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .amount(1000L)
            .build();

        LocalDateTime now = LocalDateTime.now();
        Trade trade = Trade.builder()
            .account(account)
            .tradeDateTime(now)
            .tradeAmount(100L)
            .tradeType(TradeType.DEPOSIT)
            .build();

        // then
        assertThrows(InvalidDataAccessApiUsageException.class,
            () -> tradeRepository.save(trade));
    }

    @Test
    @DisplayName("거래 생성 비정상 테스트 - 마이너스 거래 금액")
    void createTradeWithMinusTradeAmount() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .amount(1000L)
            .build();
        Account savedAccount = accountRepository.save(account);

        LocalDateTime now = LocalDateTime.now();
        Trade trade = Trade.builder()
            .account(savedAccount)
            .tradeDateTime(now)
            .tradeAmount(-100L)
            .tradeType(TradeType.DEPOSIT)
            .build();

        // then
        assertThrows(ConstraintViolationException.class,
            () -> tradeRepository.save(trade));
    }

    @Test
    @DisplayName("거래 생성 비정상 테스트 - 거래일시 미존재")
    void createTradeWithNoTradeDateTime() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .amount(1000L)
            .build();
        Account savedAccount = accountRepository.save(account);

        Trade trade = Trade.builder()
            .account(savedAccount)
            .tradeAmount(100L)
            .tradeType(TradeType.DEPOSIT)
            .build();

        // then
        assertThrows(DataIntegrityViolationException.class,
            () -> tradeRepository.save(trade));
    }

    @Test
    @DisplayName("거래 생성 비정상 테스트 - 거래구분 미존재")
    void createTradeWithNoTradeType() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = memberRepository.save(member);

        Account account = Account.builder()
            .member(savedMember)
            .amount(1000L)
            .build();
        Account savedAccount = accountRepository.save(account);

        LocalDateTime now = LocalDateTime.now();
        Trade trade = Trade.builder()
            .account(savedAccount)
            .tradeDateTime(now)
            .tradeAmount(100L)
            .build();

        // then
        assertThrows(DataIntegrityViolationException.class,
            () -> tradeRepository.save(trade));
    }

}