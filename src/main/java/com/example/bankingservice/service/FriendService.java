package com.example.bankingservice.service;

import com.example.bankingservice.domain.entity.friend.Friend;
import com.example.bankingservice.domain.entity.member.Member;
import com.example.bankingservice.domain.repository.FriendRepository;
import com.example.bankingservice.domain.repository.MemberRepository;
import com.example.bankingservice.exception.friend.SelfAdditionException;
import com.example.bankingservice.exception.friend.UnregisteredFriendException;
import com.example.bankingservice.exception.friend.UnregisteredMemberException;
import com.example.bankingservice.view.dto.FriendDto;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    private final MemberRepository memberRepository;

    public FriendDto addFriend(FriendDto friendDto) {
        validateMemberRegistration(friendDto);
        validateFriendRegistration(friendDto);
        validateSelfAddition(friendDto);
        return FriendDto.friendOf(friendRepository.save(friendDto.asFriend()));
    }

    private void validateMemberRegistration(FriendDto friendDto) {
        if (!memberRepository.existsById(Optional.ofNullable(friendDto.getMember())
            .orElse(Member.builder().build())
            .getId())) {
            throw new UnregisteredMemberException("가입되어 있지 않은 회원입니다.");
        }
    }

    private void validateFriendRegistration(FriendDto friendDto) {
        if (!memberRepository.existsById(Optional.ofNullable(friendDto.getFriend())
            .orElse(Member.builder().build())
            .getId())) {
            throw new UnregisteredFriendException("가입되어 있지 않은 친구회원입니다.");
        }
    }

    private void validateSelfAddition(FriendDto friendDto) {
        if (Optional.ofNullable(friendDto.getMember())
            .orElse(Member.builder().build())
            .getId()
            .equals(Optional.ofNullable(friendDto.getFriend())
                .orElse(Member.builder().build())
                .getId())) {
            throw new SelfAdditionException("사용자 본인은 친구로 등록할 수 없습니다.");
        }
    }

    public FriendDto readFriends(FriendDto friendDto) {
        validateMemberRegistration(friendDto);
        List<Friend> friendList = friendRepository.findAllByMemberId(friendDto.getMember().getId());
        return FriendDto.builder()
            .friends(friendList.stream()
                .map(Friend::getFriend)
                .collect(Collectors.toList()))
            .build();
    }
}
