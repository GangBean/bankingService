package com.example.bankingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.account.Trade;
import com.example.bankingservice.domain.entity.account.Trade.TradeType;
import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.repository.AccountRepository;
import com.example.bankingservice.domain.repository.TradeRepository;
import com.example.bankingservice.view.dto.TradeMakeDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    TradeRepository tradeRepository;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    TradeService tradeService;

    Logger logger = LoggerFactory.getLogger(TradeServiceTest.class);

    @Test
    @DisplayName("계좌 이체 정상 테스트")
    void makeTrade() {
        // given
        String userName1 = "유저1";
        String loginId1 = "아이디1";
        Member member1 = Member.builder()
            .id(1L)
            .userName(userName1)
            .loginId(loginId1)
            .build();

        Account account1 = Account.builder()
            .id(1L)
            .member(member1)
            .amount(10000L)
            .build();

        LocalDateTime now = LocalDateTime.now();
        Trade trade1 = Trade.builder()
            .id(1L)
            .account(account1)
            .tradeDateTime(now)
            .tradeAmount(1000L)
            .tradeType(TradeType.WITHDRAWAL)
            .build();

        String userName2 = "유저1";
        String loginId2 = "아이디1";
        Member member2 = Member.builder()
            .id(2L)
            .userName(userName2)
            .loginId(loginId2)
            .build();

        Account account2 = Account.builder()
            .id(2L)
            .member(member2)
            .amount(0L)
            .build();

        Trade trade2 = Trade.builder()
            .id(2L)
            .account(account2)
            .tradeDateTime(now)
            .tradeAmount(1000L)
            .tradeType(TradeType.DEPOSIT)
            .build();

        // when
        TradeMakeDto tradeMakeDto = TradeMakeDto.builder()
            .withdrawAccount(account1)
            .depositAccount(account2)
            .tradeAmount(1000L)
            .build();
        when(accountRepository.save(any())).thenReturn(account1).thenReturn(account2);
        when(tradeRepository.save(any())).thenReturn(trade1).thenReturn(trade2);
        when(accountRepository.existsById(any())).thenReturn(true).thenReturn(true);
        TradeMakeDto savedTrade = tradeService.makeTrade(tradeMakeDto);

        // then
        assertThat(savedTrade.getWithdrawAccount()).isSameAs(account1);
        assertThat(savedTrade.getWithdrawAccount().getAmount()).isEqualTo(9000L);
        assertThat(savedTrade.getDepositAccount()).isSameAs(account2);
        assertThat(savedTrade.getDepositAccount().getAmount()).isEqualTo(1000L);
        assertThat(savedTrade.getWithdrawTrade().getTradeDateTime()).isEqualTo(
            savedTrade.getDepositTrade().getTradeDateTime());
        assertThat(savedTrade.getWithdrawTrade().getTradeType()).isEqualTo(TradeType.WITHDRAWAL);
        assertThat(savedTrade.getDepositTrade().getTradeType()).isEqualTo(TradeType.DEPOSIT);
    }

    @Test
    @DisplayName("계좌 이체 비정상 테스트 - 출금계좌, 입금계좌 동일")
    void makeTradeWithSameAccount() {
        // given
        String userName1 = "유저1";
        String loginId1 = "아이디1";
        Member member1 = Member.builder()
            .id(1L)
            .userName(userName1)
            .loginId(loginId1)
            .build();

        Account account1 = Account.builder()
            .id(1L)
            .member(member1)
            .amount(10000L)
            .build();

        // when
        TradeMakeDto tradeMakeDto = TradeMakeDto.builder()
            .withdrawAccount(account1)
            .depositAccount(account1)
            .tradeAmount(1000L)
            .build();
        given(accountRepository.existsById(any())).willReturn(true).willReturn(true);

        // then
        assertThat(assertThrows(RuntimeException.class,
            () -> tradeService.makeTrade(tradeMakeDto)).getMessage())
            .isEqualTo("출금계좌와 입금계좌는 동일할 수 없습니다.");
    }

    @Test
    @DisplayName("계좌 이체 비정상 테스트 - 출금계좌미존재")
    void makeTradeWithdrawAccountNotExists() {
        // given
        String userName1 = "유저1";
        String loginId1 = "아이디1";
        Member member1 = Member.builder()
            .id(1L)
            .userName(userName1)
            .loginId(loginId1)
            .build();

        Account account1 = Account.builder()
            .id(1L)
            .member(member1)
            .amount(10000L)
            .build();

        String userName2 = "유저1";
        String loginId2 = "아이디1";
        Member member2 = Member.builder()
            .id(2L)
            .userName(userName2)
            .loginId(loginId2)
            .build();

        Account account2 = Account.builder()
            .id(2L)
            .member(member2)
            .amount(0L)
            .build();

        // when
        TradeMakeDto tradeMakeDto = TradeMakeDto.builder()
            .withdrawAccount(account1)
            .depositAccount(account2)
            .tradeAmount(1000L)
            .build();
        when(accountRepository.existsById(any())).thenReturn(false);

        // then
        assertThat(assertThrows(RuntimeException.class,
            () -> tradeService.makeTrade(tradeMakeDto)).getMessage())
            .isEqualTo("출금계좌가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("계좌 이체 비정상 테스트 - 입금계좌미존재")
    void makeTradeDepositAccountNotExists() {
        // given
        String userName1 = "유저1";
        String loginId1 = "아이디1";
        Member member1 = Member.builder()
            .id(1L)
            .userName(userName1)
            .loginId(loginId1)
            .build();

        Account account1 = Account.builder()
            .id(1L)
            .member(member1)
            .amount(10000L)
            .build();

        String userName2 = "유저1";
        String loginId2 = "아이디1";
        Member member2 = Member.builder()
            .id(2L)
            .userName(userName2)
            .loginId(loginId2)
            .build();

        Account account2 = Account.builder()
            .id(2L)
            .member(member2)
            .amount(0L)
            .build();

        // when
        TradeMakeDto tradeMakeDto = TradeMakeDto.builder()
            .withdrawAccount(account1)
            .depositAccount(account2)
            .tradeAmount(1000L)
            .build();
        when(accountRepository.existsById(any())).thenReturn(true).thenReturn(false);

        // then
        assertThat(assertThrows(RuntimeException.class,
            () -> tradeService.makeTrade(tradeMakeDto)).getMessage())
            .isEqualTo("입금계좌가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("계좌 이체 비정상 테스트 - 출금계좌잔액부족")
    void makeTradeWithNotEnoughWithdrawAmount() {
        // given
        String userName1 = "유저1";
        String loginId1 = "아이디1";
        Member member1 = Member.builder()
            .id(1L)
            .userName(userName1)
            .loginId(loginId1)
            .build();

        Account account1 = Account.builder()
            .id(1L)
            .member(member1)
            .amount(10000L)
            .build();

        String userName2 = "유저1";
        String loginId2 = "아이디1";
        Member member2 = Member.builder()
            .id(2L)
            .userName(userName2)
            .loginId(loginId2)
            .build();

        Account account2 = Account.builder()
            .id(2L)
            .member(member2)
            .amount(0L)
            .build();

        // when
        TradeMakeDto tradeMakeDto = TradeMakeDto.builder()
            .withdrawAccount(account1)
            .depositAccount(account2)
            .tradeAmount(100000L)
            .build();
        when(accountRepository.existsById(any())).thenReturn(true).thenReturn(true);
        when(accountRepository.existsById(any())).thenReturn(true).thenReturn(true);

        // then
        assertThat(assertThrows(RuntimeException.class,
            () -> tradeService.makeTrade(tradeMakeDto)).getMessage())
            .isEqualTo("출금계좌 잔액이 거래요청금액보다 작습니다.");
    }

}