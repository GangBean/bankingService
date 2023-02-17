package com.example.bankingservice.domain.repository;

import com.example.bankingservice.domain.entity.member.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {

}
