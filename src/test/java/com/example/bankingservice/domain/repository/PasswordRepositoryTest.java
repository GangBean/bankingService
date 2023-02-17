package com.example.bankingservice.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.entity.member.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PasswordRepositoryTest {

    @Autowired
    PasswordRepository passwordRepository;

    @Test
    @DisplayName("패스워드 생성 DB 저장 정상 테스트")
    void passwordCreateSaveTest() {
        // given
        String userName = "테스트";
        String loginId = "아이디";
        Long serialNumber = 1L;
        String hashValue = "123456789";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();

        Password password = Password.builder()
            .member(member)
            .serialNumber(serialNumber)
            .hashValue(hashValue)
            .build();

        // when
        Password savedPassword = passwordRepository.save(password);

        // then
        System.out.println(savedPassword);
        assertThat(savedPassword).isSameAs(password);
    }

    @Test
    @DisplayName("패스워드 생성 후 정상 조회 테스트")
    void passwordReadAfterSave() {
        // given
        String userName = "테스트";
        String loginId = "아이디";
        Long serialNumber = 1L;
        String hashValue = "123456789";
        Member member = Member.builder()
            .userName(userName)
            .loginId(loginId)
            .build();

        Password password = Password.builder()
            .member(member)
            .serialNumber(serialNumber)
            .hashValue(hashValue)
            .build();

        Password savedPassword = passwordRepository.save(password);

        // when
        Password readPassword = passwordRepository.findById(savedPassword.getId()).get();

        // then
        System.out.println(readPassword);
        assertThat(readPassword.getMember()).isSameAs(member);
        assertThat(readPassword.getHashValue()).isEqualTo(hashValue);
    }
}