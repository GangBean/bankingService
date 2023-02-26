package com.example.bankingservice.view.dto;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.account.Trade;
import com.example.bankingservice.domain.entity.account.Trade.TradeType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeDto {

    private Long id;

    private Account account;

    private LocalDateTime tradeDateTime;

    private Long tradeAmount;

    private TradeType tradeType;

    public Trade asTrade() {
        Trade trade = new Trade();
        BeanUtils.copyProperties(this, trade);
        return trade;
    }

    public static TradeDto tradeOf(Trade trade) {
        TradeDto tradeDto = new TradeDto();
        BeanUtils.copyProperties(trade, tradeDto);
        return tradeDto;
    }

}
