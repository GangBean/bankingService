package com.example.bankingservice.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bankingservice.domain.entity.member.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository repository;

    @Test
    @DisplayName("회원 생성 DB 정상 저장 테스트")
    void memberCreateTest() {
        // given
        String userName = "테스트";
        String loginId = "login";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();

        // when
        Member savedMember = repository.save(member);

        // then
        assertThat(savedMember).isSameAs(member);
    }

    @Test
    @DisplayName("회원 생성 비정상 테스트-로그인 아이디 없음")
    void createMemberWithNoLoginId() {
        // given
        String userName = "테스트";
        Member member = Member.builder()
            .userName(userName)
            .build();

        // then
        Assertions.assertThrows(DataIntegrityViolationException.class,
            () -> repository.save(member));
    }

    @Test
    @DisplayName("회원 저장 DB 정상 조회 테스트")
    void memberSavedDataRead() {
        // given
        String userName = "테스트";
        String loginId = "login";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = repository.save(member);

        // when
        Member readMember = repository.findById(savedMember.getId()).get();

        // then
        assertThat(readMember.getUserName()).isEqualTo(userName);
        assertThat(readMember.getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("로그인 아이디로 멤버 찾기")
    void memberFindDistinctByLoginId() {
        // given
        String userName = "테스트";
        String loginId = "login";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();
        Member savedMember = repository.save(member);

        // when
        Member duplicateMember = repository
            .findDistinctByLoginId(savedMember.getLoginId()).get();

        // then
        assertThat(duplicateMember.getLoginId()).isEqualTo(loginId);
    }
}