package com.example.bankingservice.service;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.account.Trade;
import com.example.bankingservice.domain.entity.account.Trade.TradeType;
import com.example.bankingservice.domain.repository.AccountRepository;
import com.example.bankingservice.domain.repository.TradeRepository;
import com.example.bankingservice.exception.trade.DepositAccountNotExistsException;
import com.example.bankingservice.exception.trade.NotEnoughAmountException;
import com.example.bankingservice.exception.trade.WithdrawAccountNotExistsException;
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

        validateExistsAccounts(tradeMakeDto);
        validateSameAccounts(tradeMakeDto);
        validateTradeAmount(tradeMakeDto);

        LocalDateTime tradeDateTime = LocalDateTime.now();
        Trade withdrawTrade = makeWithdrawTrade(tradeMakeDto, tradeDateTime);
        Trade depositTrade = makeDepositTrade(tradeMakeDto, tradeDateTime);

        return TradeMakeDto.builder()
            .withdrawAccount(withdrawTrade.getAccount())
            .depositAccount(depositTrade.getAccount())
            .withdrawTrade(withdrawTrade)
            .depositTrade(depositTrade)
            .tradeAmount(tradeMakeDto.getTradeAmount())
            .build();
    }

    private void validateExistsAccounts(TradeMakeDto tradeMakeDto) {
        if (!accountRepository.existsById(tradeMakeDto.getWithdrawAccount().getId())) {
            throw new WithdrawAccountNotExistsException("출금계좌가 존재하지 않습니다.");
        }

        if (!accountRepository.existsById(tradeMakeDto.getDepositAccount().getId())) {
            throw new DepositAccountNotExistsException("입금계좌가 존재하지 않습니다.");
        }
    }

    private void validateSameAccounts(TradeMakeDto tradeMakeDto) {
        if (tradeMakeDto.getWithdrawAccount().getId() == tradeMakeDto.getDepositAccount().getId()) {
            throw new RuntimeException("출금계좌와 입금계좌는 동일할 수 없습니다.");
        }
    }

    private void validateTradeAmount(TradeMakeDto tradeMakeDto) {
        if (tradeMakeDto.getWithdrawAccount().getAmount() < tradeMakeDto.getTradeAmount()) {
            throw new NotEnoughAmountException("출금계좌 잔액이 거래요청금액보다 작습니다.");
        }
    }

    private Trade makeWithdrawTrade(TradeMakeDto tradeMakeDto, LocalDateTime tradeDateTime) {
        Long tradeAmount = tradeMakeDto.getTradeAmount();
        Account withdrawAccount = tradeMakeDto.getWithdrawAccount();
        withdrawAccount.setAmount(withdrawAccount.getAmount() - tradeAmount);
        Account savedWithdraw = accountRepository.save(withdrawAccount);
        return tradeRepository.save(Trade.builder()
            .account(savedWithdraw)
            .tradeDateTime(tradeDateTime)
            .tradeType(TradeType.WITHDRAWAL)
            .tradeAmount(tradeAmount)
            .build()
        );
    }

    private Trade makeDepositTrade(TradeMakeDto tradeMakeDto, LocalDateTime tradeDateTime) {
        Long tradeAmount = tradeMakeDto.getTradeAmount();
        Account depositAccount = tradeMakeDto.getDepositAccount();
        depositAccount.setAmount(depositAccount.getAmount() + tradeAmount);
        Account savedDeposit = accountRepository.save(depositAccount);
        return tradeRepository.save(Trade.builder()
            .account(savedDeposit)
            .tradeDateTime(tradeDateTime)
            .tradeType(TradeType.DEPOSIT)
            .tradeAmount(tradeAmount)
            .build()
        );
    }

}
