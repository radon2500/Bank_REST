package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.BusinessException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final CardService cardService;

    public TransferService(CardService cardService) {
        this.cardService = cardService;
    }

    @Transactional
    public void transfer(@NonNull Long fromCardId, @NonNull Long toCardId, BigDecimal amount, String username) {
        if (amount.signum() <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }
        Card from = cardService.getCard(fromCardId);
        Card to = cardService.getCard(toCardId);
        if (!from.getOwner().getUsername().equals(username) || !to.getOwner().getUsername().equals(username)) {
            throw new BusinessException("Transfers are allowed only between your own cards");
        }
        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("Both cards must be active");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient funds");
        }
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
    }
}
