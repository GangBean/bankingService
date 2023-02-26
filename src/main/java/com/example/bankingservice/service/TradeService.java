package com.example.bankingservice.service;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.account.Trade;
import com.example.bankingservice.domain.entity.account.Trade.TradeType;
import com.example.bankingservice.domain.repository.AccountRepository;
import com.example.bankingservice.domain.repository.TradeRepository;
import com.example.bankingservice.view.dto.TradeMakeDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final AccountRepository accountRepository;

    private final TradeRepository tradeRepository;

    public TradeMakeDto makeTrade(TradeMakeDto tradeMakeDto) {
        if (tradeMakeDto.getWithdrawAccount().getId() == tradeMakeDto.getDepositAccount().getId()) {
            throw new RuntimeException("출금계좌와 입금계좌는 동일할 수 없습니다.");
        }

        if (!accountRepository.existsById(tradeMakeDto.getWithdrawAccount().getId())) {
            throw new RuntimeException("출금계좌가 존재하지 않습니다.");
        }

        if (!accountRepository.existsById(tradeMakeDto.getDepositAccount().getId())) {
            throw new RuntimeException("입금계좌가 존재하지 않습니다.");
        }

        if (tradeMakeDto.getWithdrawAccount().getAmount() < tradeMakeDto.getTradeAmount()) {
            throw new RuntimeException("출금계좌 잔액이 거래요청금액보다 작습니다.");
        }

        Long tradeAmount = tradeMakeDto.getTradeAmount();
        Account withdrawAccount = tradeMakeDto.getWithdrawAccount();
        Account depositAccount = tradeMakeDto.getDepositAccount();

        withdrawAccount.setAmount(withdrawAccount.getAmount() - tradeAmount);
        depositAccount.setAmount(depositAccount.getAmount() + tradeAmount);

        Account savedWithdraw = accountRepository.save(withdrawAccount);
        Account savedDeposit = accountRepository.save(depositAccount);

        LocalDateTime tradeDateTime = LocalDateTime.now();
        Trade withdrawTrade = tradeRepository.save(Trade.builder()
            .account(savedWithdraw)
            .tradeDateTime(tradeDateTime)
            .tradeType(TradeType.WITHDRAWAL)
            .build()
        );

        Trade depositTrade = tradeRepository.save(Trade.builder()
            .account(savedDeposit)
            .tradeDateTime(tradeDateTime)
            .tradeType(TradeType.DEPOSIT)
            .build()
        );

        return TradeMakeDto.builder()
            .withdrawAccount(savedWithdraw)
            .depositAccount(savedDeposit)
            .withdrawTrade(withdrawTrade)
            .depositTrade(depositTrade)
            .tradeAmount(tradeAmount)
            .build();
    }

}
