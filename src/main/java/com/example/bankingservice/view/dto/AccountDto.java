package com.example.bankingservice.view.dto;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AccountDto {

    private Long id;

    private Member member;

    private Long amount;

    public Account asAccount() {
        Account account = new Account();
        BeanUtils.copyProperties(this, account);
        return account;
    }

    public static AccountDto accountOf(Account account) {
        AccountDto accountDto = new AccountDto();
        BeanUtils.copyProperties(account, accountDto);
        return accountDto;
    }
}
