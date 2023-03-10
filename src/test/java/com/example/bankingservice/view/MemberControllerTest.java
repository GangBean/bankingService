package com.example.bankingservice.view;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.exception.member.DuplicateLoginIdException;
import com.example.bankingservice.exception.member.MemberException;
import com.example.bankingservice.service.AccountService;
import com.example.bankingservice.service.FriendService;
import com.example.bankingservice.service.MemberService;
import com.example.bankingservice.view.dto.AccountDto;
import com.example.bankingservice.view.dto.FriendDto;
import com.example.bankingservice.view.dto.MemberDto;
import java.util.ArrayList;
import java.util.List;
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

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    MemberService memberService;

    @MockBean
    FriendService friendService;

    @MockBean
    AccountService accountService;

    Logger logger = LoggerFactory.getLogger(MemberControllerTest.class);

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    void joinMember() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "??????";

        // when
        given(memberService.join(any()))
            .willReturn(MemberDto.builder()
                .id(1L)
                .userName(userName)
                .loginId(loginId)
                .build());

        ResultActions perform = mvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(
                "{ \"userName\" : \"??????\", \"loginId\" : \"box1234\"} , \"password\" :\"12345\""));

        // then
        perform.andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.password").isEmpty())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ????????? ????????? - ????????? ?????? ??????")
    void joinMemberDuplicateLoginId() throws Exception {

        // when
        given(memberService.join(any()))
            .willThrow(new DuplicateLoginIdException("????????? ID??? ?????? ???????????????."));

        ResultActions perform = mvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(
                "{ \"userName\" : \"??????\", \"loginId\" : \"box1234\"} , \"password\" :\"12345\""));

        // then
        perform.andExpect(status().isConflict())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ????????? ????????? - ?????? ?????? ??????")
    void joinMemberServerInternalError() throws Exception {

        // when
        given(memberService.join(any()))
            .willThrow(new MemberException());

        ResultActions perform = mvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(
                "{ \"userName\" : \"??????\", \"loginId\" : \"box1234\"} , \"password\" :\"12345\"}"));

        // then
        perform.andExpect(status().isInternalServerError())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    void addFriend() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "??????";
        String loginId2 = "box1235";
        String userName2 = "?????????";
        String nickname = "??????";

        // when
        given(memberService.findById(any()))
            .willReturn(MemberDto.builder()
                .id(1L)
                .build());

        given(friendService.addFriend(any()))
            .willReturn(FriendDto.builder()
                .member(Member.builder()
                    .id(1L)
                    .loginId(loginId)
                    .userName(userName)
                    .build())
                .friend(Member.builder()
                    .id(2L)
                    .loginId(loginId2)
                    .userName(userName2)
                    .build())
                .nickName(nickname)
                .build());

        ResultActions perform = mvc.perform(post("/members/{id}/friends", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(
                "{ \"friend\" : { \"id\" : \"2\", \"userName\" : \"?????????\""
                    + ", \"loginId\" : \"box1235\" }"
                    + ", \"nickname\" : \"??????\" }"));

        // then
        perform.andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.member.id").value(1L))
            .andExpect(jsonPath("$.friend.id").value(2L))
            .andExpect(jsonPath("$.nickName").value(nickname))
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ????????? ????????? - ?????? ?????? ??????")
    void addFriendSelf() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "??????";
        String nickname = "??????";

        // when
        given(memberService.findById(any()))
            .willReturn(MemberDto.builder()
                .id(1L)
                .build());

        given(friendService.addFriend(any())).willThrow(new RuntimeException());

        ResultActions perform = mvc.perform(post("/members/{id}/friends", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(
                "{ \"friend\" : { \"id\" : \"1\", \"userName\" : \"??????\""
                    + ", \"loginId\" : \"box1234\" }"
                    + ", \"nickname\" : \"??????\" }"));

        // then
        perform.andExpect(status().isConflict())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    void readFriends() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "??????";
        String nickname = "??????";

        List<Member> list = new ArrayList<>();
        list.add(Member.builder()
            .id(2L)
            .loginId(loginId)
            .userName(userName)
            .build());

        FriendDto friendDto = FriendDto.builder()
            .member(Member.builder()
                .id(1L)
                .loginId("xbox123")
                .userName("??????")
                .build())
            .friends(list)
            .nickName(nickname)
            .build();

        // when
        given(friendService.readFriends(any())).willReturn(friendDto);

        ResultActions perform = mvc.perform(get("/members/{id}/friends", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content("{}"));

        // then
        perform.andExpect(status().isOk())
            .andExpect(jsonPath("$.friends").isNotEmpty())
            .andExpect(jsonPath("$.friends.size()").value(1))
            .andExpect(jsonPath("$.member.loginId").value("xbox123"))
            .andExpect(jsonPath("$.member.userName").value("??????"))
            .andExpect(jsonPath("$.nickName").value(nickname))
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ????????? ????????? - ?????? ?????? ?????? ?????????")
    void readFriendsFailureRequestUserNotMember() throws Exception {
        // when
        given(friendService.readFriends(any())).willThrow(new RuntimeException());

        ResultActions perform = mvc.perform(get("/members/{id}/friends", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content("{}"));

        // then
        perform.andExpect(status().isNotFound())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????????")
    void readAccounts() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "??????";
        String nickname = "??????";

        // when
        given(accountService.readAccounts(any())).willReturn(AccountDto.builder()
            .id(1L)
            .member(Member.builder()
                .id(1L)
                .loginId(loginId)
                .userName(userName)
                .build())
            .amount(10_000L)
            .build());

        ResultActions perform = mvc.perform(get("/members/{id}/accounts", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content("{}"));

        // then
        perform.andExpect(status().isOk())
            .andExpect(jsonPath("$.member.id").value(1L))
            .andExpect(jsonPath("$.member.loginId").value(loginId))
            .andExpect(jsonPath("$.member.userName").value(userName))
            .andExpect(jsonPath("$.amount").value(10_000L))
            .andExpect(jsonPath("$.id").value(1L))
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("?????? ?????? ????????? ????????? - ?????? ?????? ?????? ?????????")
    void readAccountsWithNotRegisteredMember() throws Exception {
        // when
        given(accountService.readAccounts(any())).willThrow(new RuntimeException());

        ResultActions perform = mvc.perform(get("/members/{id}/accounts", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content("{}"));

        // then
        perform.andExpect(status().isNotFound())
            .andDo(MockMvcResultHandlers.print())
        ;
    }
}