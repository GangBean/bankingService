package com.example.bankingservice.service;

import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.entity.member.Password;
import com.example.bankingservice.domain.repository.MemberRepository;
import com.example.bankingservice.domain.repository.PasswordRepository;
import com.example.bankingservice.view.dto.MemberDto;
import com.example.bankingservice.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordRepository passwordRepository;

    public MemberDto join(MemberDto memberDto) throws Exception {
        validateLoginIdDuplicate(memberDto.getLoginId());
        Member savedMember = memberRepository.save(memberDto.asMember());

        createPassword(memberDto, savedMember);

        return MemberDto.builder()
            .id(savedMember.getId())
            .userName(savedMember.getUserName())
            .loginId(savedMember.getLoginId())
            .build();
    }

    public MemberDto findById(Long id) {
        return MemberDto.memberOf(memberRepository.findById(id)
            .orElse(Member.builder().build()));
    }

    private void validateLoginIdDuplicate(String loginId) {
        if (memberRepository.findDistinctByLoginId(loginId).isPresent()) {
            throw new RuntimeException("로그인 ID가 이미 존재합니다.");
        }
    }

    private void createPassword(MemberDto memberDto, Member savedMember) {
        Long passwordSerialNumber = this.pickNextSerialNumber(memberDto.getLoginId());
        passwordRepository.save(Password.builder()
            .member(savedMember)
            .serialNumber(passwordSerialNumber)
            .hashValue(encryptPassword(memberDto.getPassword()))
            .build());
    }

    private Long pickNextSerialNumber(String loginId) {
        return (passwordRepository.findPasswordByMemberLoginIdOrderBySerialNumberDesc(loginId)
            .orElse(Password.builder().serialNumber(0L).build())
            .getSerialNumber() + 1L);
    }

    public String encryptPassword(String password) {
        return EncryptUtil.getEncrypt(password);
    }
}
