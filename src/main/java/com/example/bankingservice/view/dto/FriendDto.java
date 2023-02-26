package com.example.bankingservice.view.dto;


import com.example.bankingservice.domain.entity.friend.Friend;
import com.example.bankingservice.domain.entity.member.Member;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FriendDto {

    private Long id;

    @NotBlank(message = "회원 정보는 필수입니다.")
    private Member member;

    @NotBlank(message = "친구 정보는 필수입니다.")
    private Member friend;

    private String nickName;

    private List<Member> friends;

    public Friend asFriend() {
        Friend friend = new Friend();
        BeanUtils.copyProperties(this, friend);
        return friend;
    }

    public static FriendDto friendOf(Friend friend) {
        FriendDto friendDto = new FriendDto();
        BeanUtils.copyProperties(friend, friendDto);
        return friendDto;
    }

}
