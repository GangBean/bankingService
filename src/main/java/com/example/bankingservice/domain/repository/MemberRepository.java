package com.example.bankingservice.domain.repository;

import com.example.bankingservice.domain.entity.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findDistinctByLoginId(String loginId);
}
