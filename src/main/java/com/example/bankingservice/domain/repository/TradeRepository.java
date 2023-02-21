package com.example.bankingservice.domain.repository;

import com.example.bankingservice.domain.entity.account.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

}
