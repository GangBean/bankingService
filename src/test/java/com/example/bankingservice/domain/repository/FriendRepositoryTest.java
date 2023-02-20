package com.example.bankingservice.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.bankingservice.domain.entity.friend.Friend;
import com.example.bankingservice.domain.entity.member.Member;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class FriendRepositoryTest {

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    MemberRepository memberRepository;

    private Logger log = LoggerFactory.getLogger(FriendRepositoryTest.class);

    @Test
    @DisplayName("친구 생성 정상 테스트")
    void saveFriendSuccess() {
        // given
        String userName = "테스트";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember1 = memberRepository.save(member);

        String friendName = "친구";
        String friendId = "친구아이디";
        Member member2 = Member.builder()
            .userName(friendName)
            .loginId(friendId)
            .build();
        Member savedMember2 = memberRepository.save(member2);

        log.info(savedMember1.toString());

        String nickname = "로미";
        Friend friend = Friend.builder()
            .member(savedMember1)
            .friend(savedMember2)
            .nickName(nickname)
            .build();

        // when
        Friend saveFriend = friendRepository.save(friend);
        log.info(saveFriend.toString());

        // then
        assertThat(saveFriend.getMember()).isSameAs(member);
        assertThat(saveFriend.getFriend()).isSameAs(member2);
        assertThat(saveFriend.getNickName()).isEqualTo(nickname);

    }

    @Test
    @DisplayName("친구 생성 실패 테스트 - 미존재 회원 등록")
    void saveFriendNoMemberFailure() {
        // given
        String userName = "테스트";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember1 = memberRepository.save(member);

        String friendName = "친구";
        String friendId = "친구아이디";
        Member unsavedMember = Member.builder()
            .id(100L)
            .userName(friendName)
            .loginId(friendId)
            .build();

        String nickname = "로미";
        Friend friend = Friend.builder()
            .member(savedMember1)
            .friend(unsavedMember)
            .nickName(nickname)
            .build();

        // when

        // then
        assertThrows(DataIntegrityViolationException.class, () -> friendRepository.save(friend));

    }

    @Test
    @DisplayName("친구 정상 조회 테스트")
    void readFriend() {
        // given
        String userName = "테스트";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember1 = memberRepository.save(member);

        String friendName = "친구";
        String friendId = "친구아이디";
        Member member2 = Member.builder()
            .userName(friendName)
            .loginId(friendId)
            .build();
        Member savedMember2 = memberRepository.save(member2);

        String nickname = "로미";
        Friend friend = Friend.builder()
            .member(savedMember1)
            .friend(savedMember2)
            .nickName(nickname)
            .build();

        Friend savedFriend = friendRepository.save(friend);

        // when
        Friend readFriend = friendRepository.findById(savedFriend.getId()).get();

        // then
        assertThat(readFriend).isSameAs(savedFriend);

    }

    @Test
    @DisplayName("유저ID로 친구 리스트 조회")
    void readFriendListByMemberId() {
        // given
        String userName = "사용자";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();

        String userName2 = "사용자2";
        String loginId2 = "아이디2";
        Member member2 = Member.builder()
            .userName(userName2)
            .loginId(loginId2)
            .build();

        String userName3 = "사용자3";
        String loginId3 = "아이디3";
        Member member3 = Member.builder()
            .userName(userName3)
            .loginId(loginId3)
            .build();

        Member savedMember = memberRepository.save(member);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);

        Friend friend1 = Friend.builder()
            .member(savedMember)
            .friend(savedMember2)
            .build();

        Friend friend2 = Friend.builder()
            .member(savedMember)
            .friend(savedMember3)
            .build();

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        // when
        List<Friend> friendList = friendRepository.findAllByMemberId(savedMember.getId());
        List<Member> friendMemberList = friendList.stream()
            .map(Friend::getFriend)
            .collect(Collectors.toList());

        // then
        log.info(friendList.toString());
        log.info(friendMemberList.toString());
        assertThat(friendMemberList).contains(member2);
        assertThat(friendMemberList).contains(member3);
    }

    @Test
    @DisplayName("유저ID로 친구 리스트 조회 - 대상 없음")
    void readFriendListByMemberIdWithNoFriend() {
        // given
        String userName = "사용자";
        String loginId = "아이디";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();

        String userName2 = "사용자2";
        String loginId2 = "아이디2";
        Member member2 = Member.builder()
            .userName(userName2)
            .loginId(loginId2)
            .build();

        String userName3 = "사용자3";
        String loginId3 = "아이디3";
        Member member3 = Member.builder()
            .userName(userName3)
            .loginId(loginId3)
            .build();

        Member savedMember = memberRepository.save(member);
        Member savedMember2 = memberRepository.save(member2);
        Member savedMember3 = memberRepository.save(member3);

        Friend friend1 = Friend.builder()
            .member(savedMember)
            .friend(savedMember2)
            .build();

        Friend friend2 = Friend.builder()
            .member(savedMember)
            .friend(savedMember3)
            .build();

        friendRepository.save(friend1);
        friendRepository.save(friend2);

        // when
        List<Friend> friendList = friendRepository.findAllByMemberId(10L);
        List<Member> friendMemberList = friendList.stream()
            .map(Friend::getFriend)
            .collect(Collectors.toList());

        // then
        assertThat(friendMemberList).hasSize(0);
    }

}