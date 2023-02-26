package com.example.bankingservice.view;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.view.dto.AccountDto;
import com.example.bankingservice.view.dto.FriendDto;
import com.example.bankingservice.view.dto.MemberDto;
import com.example.bankingservice.service.AccountService;
import com.example.bankingservice.service.FriendService;
import com.example.bankingservice.service.MemberService;
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
    @DisplayName("회원 가입 정상 테스트")
    void joinMember() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "로미";

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
                "{ \"userName\" : \"로미\", \"loginId\" : \"box1234\"} , \"password\" :\"12345\""));

        // then
        perform.andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.password").isEmpty())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("회원 가입 비정상 테스트 - 아이디 중복 오류")
    void joinMemberDuplicateLoginId() throws Exception {

        // when
        given(memberService.join(any()))
            .willThrow(new RuntimeException("로그인 ID가 이미 존재합니다."));

        ResultActions perform = mvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(
                "{ \"userName\" : \"로미\", \"loginId\" : \"box1234\"} , \"password\" :\"12345\""));

        // then
        perform.andExpect(status().isConflict())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("회원 가입 비정상 테스트 - 서버 내부 오류")
    void joinMemberServerInternalError() throws Exception {

        // when
        given(memberService.join(any()))
            .willThrow(new Exception());

        ResultActions perform = mvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .characterEncoding("UTF-8")
            .content(
                "{ \"userName\" : \"로미\", \"loginId\" : \"box1234\"} , \"password\" :\"12345\""));

        // then
        perform.andExpect(status().isInternalServerError())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("친구 등록 정상 테스트")
    void addFriend() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "로미";
        String loginId2 = "box1235";
        String userName2 = "로오미";
        String nickname = "베프";

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
                "{ \"friend\" : { \"id\" : \"2\", \"userName\" : \"로오미\""
                    + ", \"loginId\" : \"box1235\" }"
                    + ", \"nickname\" : \"베프\" }"));

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
    @DisplayName("친구 등록 비정상 테스트 - 본인 친구 등록")
    void addFriendSelf() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "로미";
        String nickname = "베프";

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
                "{ \"friend\" : { \"id\" : \"1\", \"userName\" : \"로미\""
                    + ", \"loginId\" : \"box1234\" }"
                    + ", \"nickname\" : \"베프\" }"));

        // then
        perform.andExpect(status().isConflict())
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("친구 조회 정상 테스트")
    void readFriends() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "로미";
        String nickname = "베프";

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
                .userName("미로")
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
            .andExpect(jsonPath("$.member.userName").value("미로"))
            .andExpect(jsonPath("$.nickName").value(nickname))
            .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    @DisplayName("친구 조회 비정상 테스트 - 조회 요청 회원 미가입")
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
    @DisplayName("계좌 조회 정상 테스트")
    void readAccounts() throws Exception {
        // given
        String loginId = "box1234";
        String userName = "로미";
        String nickname = "베프";

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
    @DisplayName("계좌 조회 비정상 테스트 - 조회 요청 회원 미가입")
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