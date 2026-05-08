package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findByOwnerAndNumberLast4Containing(User owner, String last4, Pageable pageable);
    Page<Card> findByNumberLast4Containing(String last4, Pageable pageable);
}
