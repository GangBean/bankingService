package com.example.bankingservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.entity.member.Password;
import com.example.bankingservice.domain.repository.MemberRepository;
import com.example.bankingservice.domain.repository.PasswordRepository;
import com.example.bankingservice.util.EncryptUtil;
import com.example.bankingservice.view.dto.MemberDto;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordRepository passwordRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    @DisplayName("회원 정보 정상 가입 테스트")
    void memberJoin() throws Exception {
        // given
        String userName = "사용자";
        String loginId = "아이디1";
        String password = "1234";
        String encryptPassword = EncryptUtil.getEncrypt(password);
        MemberDto memberDto = MemberDto.builder()
            .userName(userName)
            .loginId(loginId)
            .password(password)
            .build();

        // when
        when(memberRepository.save(any())).thenReturn(memberDto.asMember());
        when(passwordRepository.save(any())).thenReturn(
            Password.builder().hashValue(encryptPassword).build());
        MemberDto joinMember = memberService.join(memberDto);

        // then
        assertThat(joinMember.getLoginId()).isNotNull();
        assertThat(joinMember.getUserName()).isEqualTo(userName);
        assertThat(joinMember.getLoginId()).isEqualTo(loginId);
        assertThat(joinMember.getPassword()).isNull();
    }

    @Test
    @DisplayName("회원 정보 정상 조회 테스트 - id로 조회")
    void findMemberById() throws Exception {
        // given
        String userName = "사용자";
        String loginId = "아이디1";

        // when
        when(memberRepository.findById(any())).thenReturn(Optional.ofNullable(Member.builder()
                .id(1L)
                .userName(userName)
                .loginId(loginId)
            .build()));
        MemberDto joinMember = memberService.findById(1L);

        // then
        assertThat(joinMember.getLoginId()).isNotNull();
        assertThat(joinMember.getUserName()).isEqualTo(userName);
        assertThat(joinMember.getLoginId()).isEqualTo(loginId);
        assertThat(joinMember.getPassword()).isNull();
    }

    @Test
    @DisplayName("로그인 아이디 중복 가입 방지")
    void memberJoinDuplicateLoginIdFailure() {
        // given
        String userName = "사용자";
        String loginId = "아이디1";
        String password = "1234";

        MemberDto memberDto = MemberDto.builder()
            .userName(userName)
            .loginId(loginId)
            .password(password)
            .build();

        // when
        when(memberRepository.findDistinctByLoginId(loginId))
            .thenReturn(Optional.ofNullable(Member.builder().build()));

        // then
        assertEquals("로그인 ID가 이미 존재합니다.",
            assertThrows(RuntimeException.class, () -> memberService.join(memberDto))
                .getMessage());
    }

}