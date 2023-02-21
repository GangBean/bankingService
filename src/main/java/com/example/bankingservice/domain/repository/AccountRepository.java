package com.example.bankingservice.domain.repository;

import com.example.bankingservice.domain.entity.account.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberId(Long memberId);
}
