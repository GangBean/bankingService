package com.example.bankingservice.service;

import com.example.bankingservice.domain.entity.friend.Friend;
import com.example.bankingservice.domain.repository.FriendRepository;
import com.example.bankingservice.domain.view.dto.FriendDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;

    public FriendDto addFriend(FriendDto friendDto) {
        validateSelf(friendDto);
        return FriendDto.friendOf(friendRepository.save(friendDto.asFriend()));
    }

    private void validateSelf(FriendDto friendDto) {
        if (friendDto.getMember().getId() == friendDto.getFriend().getId()) {
            throw new RuntimeException("사용자 본인은 친구로 등록할 수 없습니다.");
        }
    }

    public FriendDto readFriends(FriendDto friendDto) {
        List<Friend> friendList = friendRepository.findAllByMemberId(friendDto.getMember().getId());
        return FriendDto.builder()
            .friends(friendList.stream()
                .map(Friend::getFriend)
                .collect(Collectors.toList()))
            .build();
    }
}
