package com.example.bankingservice.domain.repository;

import com.example.bankingservice.domain.entity.friend.Friend;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    List<Friend> findAllByMemberId(Long memberId);
}
