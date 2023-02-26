package com.example.bankingservice.view;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankingservice.domain.entity.account.Account;
import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.service.AccountService;
import com.example.bankingservice.service.TradeService;
import com.example.bankingservice.view.dto.AccountDto;
import com.example.bankingservice.view.dto.TradeMakeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(TradeController.class)
@MockBean(JpaMetamodelMappingContext.class)
class TradeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TradeService tradeService;

    @MockBean
    AccountService accountService;

    Logger logger = LoggerFactory.getLogger(TradeControllerTest.class);

    @Test
    @DisplayName("송금하기 정상 테스트")
    void makeTrade() throws Exception {
        // given
        Account withdraw = Account.builder()
            .id(1L)
            .member(Member.builder()
                .id(1L)
                .userName("이름1")
                .loginId("아이디1")
                .build())
            .amount(10_000L)
            .build();

        Account deposit = Account.builder()
            .id(2L)
            .member(Member.builder()
                .id(2L)
                .userName("이름2")
                .loginId("아이디2")
                .build())
            .amount(0L)
            .build();

        // when
        given(accountService.findAccountById(any()))
            .willReturn(AccountDto.accountOf(withdraw))
            .willReturn(AccountDto.accountOf(deposit));
        withdraw.setAmount(0L);
        deposit.setAmount(10_000L);

        given(tradeService.makeTrade(any()))
            .willReturn(TradeMakeDto.builder()
                .withdrawAccount(withdraw)
                .depositAccount(deposit)
                .tradeAmount(10_000L)
                .build());

        ResultActions perform = mvc.perform(post("/trades")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content("{\"withdrawAccount\" : {\"id\" : \"1\"},"
                + "\"depositAccount\" : {\"id\" : \"2\"},"
                + "\"tradeAmount\" : \"10000\"}"));

        // then
        perform.andExpect(status().isCreated())
            .andExpect(jsonPath("$.withdrawAccount.id").value(1L))
            .andExpect(jsonPath("$.withdrawAccount.amount").value(0L))
            .andExpect(jsonPath("$.depositAccount.id").value(2L))
            .andExpect(jsonPath("$.depositAccount.amount").value(10_000L))
            .andExpect(jsonPath("$.tradeAmount").value(10_000L))
            .andDo(MockMvcResultHandlers.print());
    }
}