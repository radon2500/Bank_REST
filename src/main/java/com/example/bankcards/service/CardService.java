package com.example.bankcards.service;

import com.example.bankcards.dto.CardDtos;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardCryptoUtil;
import com.example.bankcards.util.CardMaskingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardCryptoUtil cardCryptoUtil;

    public CardService(CardRepository cardRepository, UserRepository userRepository, CardCryptoUtil cardCryptoUtil) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardCryptoUtil = cardCryptoUtil;
    }

    @Transactional
    public CardDtos.CardResponse create(CardDtos.CreateCardRequest request) {
        Long ownerId = Objects.requireNonNull(request.ownerId(), "ownerId must not be null");
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));
        if (request.number().length() < 4) {
            throw new BusinessException("Invalid card number");
        }
        Card card = new Card();
        card.setOwner(owner);
        card.setEncryptedNumber(cardCryptoUtil.encrypt(request.number()));
        card.setNumberLast4(request.number().substring(request.number().length() - 4));
        card.setBalance(request.initialBalance());
        card.setExpiresAt(request.expiresAt());
        card.setCreatedAt(LocalDateTime.now());
        card.setStatus(request.expiresAt().isBefore(LocalDate.now()) ? CardStatus.EXPIRED : CardStatus.ACTIVE);
        return toResponse(Objects.requireNonNull(cardRepository.save(card), "Saved card must not be null"));
    }

    @Transactional(readOnly = true)
    public Page<CardDtos.CardResponse> ownCards(User owner, String search, Pageable pageable) {
        String value = search == null ? "" : search;
        return cardRepository.findByOwnerAndNumberLast4Containing(owner, value, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CardDtos.CardResponse ownCardById(@NonNull Long cardId, String username) {
        Card card = getCard(cardId);
        if (!card.getOwner().getUsername().equals(username)) {
            throw new BusinessException("Only owner can view this card");
        }
        return toResponse(card);
    }

    @Transactional(readOnly = true)
    public CardDtos.CardResponse cardById(@NonNull Long cardId) {
        return toResponse(getCard(cardId));
    }

    @Transactional(readOnly = true)
    public Page<CardDtos.CardResponse> allCards(String search, Pageable pageable) {
        String value = search == null ? "" : search;
        return cardRepository.findByNumberLast4Containing(value, pageable).map(this::toResponse);
    }

    @Transactional
    public CardDtos.CardResponse updateStatus(@NonNull Long cardId, CardStatus status) {
        Card card = getCard(cardId);
        card.setStatus(status);
        return toResponse(card);
    }

    @Transactional
    public void delete(@NonNull Long cardId) {
        cardRepository.delete(getCard(cardId));
    }

    @Transactional
    public CardDtos.CardResponse requestBlock(@NonNull Long cardId, String username) {
        Card card = getCard(cardId);
        if (!card.getOwner().getUsername().equals(username)) {
            throw new BusinessException("Only owner can request blocking");
        }
        card.setStatus(CardStatus.BLOCKED);
        return toResponse(card);
    }

    @Transactional(readOnly = true)
    public @NonNull Card getCard(@NonNull Long cardId) {
        return Objects.requireNonNull(
                cardRepository.findById(cardId).orElseThrow(() -> new NotFoundException("Card not found")),
                "Card must not be null"
        );
    }

    private CardDtos.CardResponse toResponse(@NonNull Card card) {
        return new CardDtos.CardResponse(
                card.getId(),
                CardMaskingUtil.maskFromLast4(card.getNumberLast4()),
                card.getOwner().getUsername(),
                card.getStatus(),
                card.getBalance(),
                card.getExpiresAt()
        );
    }
}
