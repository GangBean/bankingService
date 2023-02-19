package com.example.bankingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bankingservice.domain.entity.friend.Friend;
import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.repository.FriendRepository;
import com.example.bankingservice.domain.repository.MemberRepository;
import com.example.bankingservice.domain.view.dto.FriendDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FriendServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    FriendRepository friendRepository;

    @InjectMocks
    FriendService friendService;

    @Test
    @DisplayName("친구 목록 생성 테스트")
    void createFriend() {
        // given
        Long memberId = 1L;
        String userName = "유저";
        String loginId = "아이디";
        Member member1 = Member.builder()
            .id(memberId)
            .userName(userName)
            .loginId(loginId)
            .build();

        Long memberId2 = 2L;
        String userName2 = "유저";
        String loginId2 = "아이디";
        Member member2 = Member.builder()
            .id(memberId2)
            .userName(userName2)
            .loginId(loginId2)
            .build();

        String nickname = "닉네임";
        Friend friend = Friend.builder()
            .id(1L)
            .member(member1)
            .friend(member2)
            .nickName(nickname)
            .build();

        // when
        when(friendRepository.save(any())).thenReturn(friend);
        FriendDto saved = friendService.addFriend(FriendDto.friendOf(friend));

        // then
        assertThat(saved.getMember()).isSameAs(member1);
        assertThat(saved.getFriend()).isSameAs(member2);
        assertThat(saved.getNickName()).isEqualTo(nickname);
    }

    @Test
    @DisplayName("친구 목록 생성 실패 테스트 - 본인을 친구로 등록")
    void createSelfFriendFailure() {
        // given
        Long memberId = 1L;
        String userName = "유저";
        String loginId = "아이디";
        Member member1 = Member.builder()
            .id(memberId)
            .userName(userName)
            .loginId(loginId)
            .build();

        String nickname = "닉네임";
        Friend friend = Friend.builder()
            .id(1L)
            .member(member1)
            .friend(member1)
            .nickName(nickname)
            .build();

        // then
        assertThat(assertThrows(RuntimeException.class,
            () -> friendService.addFriend(FriendDto.friendOf(friend)))
            .getMessage()).isEqualTo("사용자 본인은 친구로 등록할 수 없습니다.");
    }

    @Test
    @DisplayName("친구목록 조회 - 여러명")
    void readFriendList() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .id(1L)
            .userName(userName)
            .loginId(loginId)
            .build();

        String userName2 = "유저명";
        String loginId2 = "아이디";
        Member member2 = Member.builder()
            .id(2L)
            .userName(userName2)
            .loginId(loginId2)
            .build();
        List<Member> memberList = new ArrayList<>(
            Arrays.asList(member, member2)
        );

        List<Friend> friendList = memberList.stream()
            .map(x->Friend.builder()
                .friend(x)
                .build())
            .collect(Collectors.toList());

        // when
        when(friendRepository.findAllByMemberId(3L)).thenReturn(friendList);
        FriendDto friends = friendService.readFriends(FriendDto.builder()
            .member(Member.builder()
                .id(3L)
                .build())
            .build());

        // then
        assertThat(friends.getFriends()).contains(member);
        assertThat(friends.getFriends()).contains(member2);
    }

    @Test
    @DisplayName("친구목록 조회 - 대상 없음")
    void readFriendListNoFriend() {
        // given
        String userName = "유저명";
        String loginId = "아이디";
        Member member = Member.builder()
            .id(1L)
            .userName(userName)
            .loginId(loginId)
            .build();

        String userName2 = "유저명";
        String loginId2 = "아이디";
        Member member2 = Member.builder()
            .id(2L)
            .userName(userName2)
            .loginId(loginId2)
            .build();
        List<Member> memberList = new ArrayList<>(
            Arrays.asList(member, member2)
        );

        List<Friend> friendList = memberList.stream()
            .map(x->Friend.builder()
                .friend(x)
                .build())
            .collect(Collectors.toList());

        // when
        when(friendRepository.findAllByMemberId(3L)).thenReturn(friendList);
        FriendDto friends = friendService.readFriends(FriendDto.builder()
            .member(Member.builder()
                .id(3L)
                .build())
            .build());

        // then
        assertThat(friends.getFriends()).contains(member);
        assertThat(friends.getFriends()).contains(member2);
    }

}
