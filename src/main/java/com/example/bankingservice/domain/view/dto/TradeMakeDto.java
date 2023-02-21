package com.example.bankingservice.domain.view.dto;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.account.Trade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeMakeDto {

    private Account withdrawAccount;

    private Account depositAccount;

    private Long tradeAmount;

    private Trade withdrawTrade;

    private Trade depositTrade;

}
