package com.example.bankingservice.view;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.service.AccountService;
import com.example.bankingservice.service.TradeService;
import com.example.bankingservice.view.dto.TradeMakeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<TradeMakeDto> makeTrade(@RequestBody TradeMakeDto tradeMakeDto) {
        Account withdraw = accountService.findAccountById(tradeMakeDto.getWithdrawAccount().getId())
            .asAccount();
        Account deposit = accountService.findAccountById(tradeMakeDto.getDepositAccount().getId())
            .asAccount();
        TradeMakeDto input = TradeMakeDto.builder()
            .withdrawAccount(withdraw)
            .depositAccount(deposit)
            .tradeAmount(tradeMakeDto.getTradeAmount())
            .build();
        return new ResponseEntity<>(tradeService.makeTrade(input), HttpStatus.CREATED);
    }

}
