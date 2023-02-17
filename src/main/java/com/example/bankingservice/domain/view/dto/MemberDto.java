package com.example.bankingservice.domain.view.dto;

import com.example.bankingservice.domain.entity.member.Member;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDto {

    private Long id;

    @NotBlank(message = "회원명은 필수입니다.")
    private String userName;

    @NotBlank(message = "로그인 아이디는 필수입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public Member asMember() {
        Member member = new Member();
        BeanUtils.copyProperties(this, member);
        return member;
    }

    public static MemberDto memberOf(Member member) {
        MemberDto memberDto = new MemberDto();
        BeanUtils.copyProperties(member, memberDto);
        return memberDto;
    }

}
