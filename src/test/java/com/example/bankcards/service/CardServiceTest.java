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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardCryptoUtil cardCryptoUtil;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCardSuccess() {
        User owner = new User();
        owner.setId(10L);
        owner.setUsername("user");
        CardDtos.CreateCardRequest request = new CardDtos.CreateCardRequest(
                10L,
                "4111111111111111",
                LocalDate.now().plusYears(1),
                new BigDecimal("150.00")
        );
        when(userRepository.findById(10L)).thenReturn(Optional.of(owner));
        when(cardCryptoUtil.encrypt("4111111111111111")).thenReturn("enc");
        when(cardRepository.save(org.mockito.ArgumentMatchers.any(Card.class))).thenAnswer(invocation -> {
            Card saved = Objects.requireNonNull(invocation.getArgument(0));
            saved.setId(1L);
            return saved;
        });

        CardDtos.CardResponse response = cardService.create(request);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).save(captor.capture());
        Card saved = Objects.requireNonNull(captor.getValue());
        assertEquals("enc", saved.getEncryptedNumber());
        assertEquals("1111", saved.getNumberLast4());
        assertEquals(CardStatus.ACTIVE, saved.getStatus());
        assertEquals(new BigDecimal("150.00"), response.balance());
        assertEquals("**** **** **** 1111", response.maskedNumber());
    }

    @Test
    void ownCardByIdFailsForAnotherUser() {
        Card card = card("owner", CardStatus.ACTIVE, "200.00");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(BusinessException.class, () -> cardService.ownCardById(1L, "intruder"));
    }

    @Test
    void requestBlockChangesStatusForOwner() {
        Card card = card("owner", CardStatus.ACTIVE, "200.00");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        CardDtos.CardResponse response = cardService.requestBlock(1L, "owner");

        assertEquals(CardStatus.BLOCKED, card.getStatus());
        assertEquals(CardStatus.BLOCKED, response.status());
    }

    @Test
    void getCardFailsWhenNotFound() {
        when(cardRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getCard(777L));
    }

    private Card card(String username, CardStatus status, String balance) {
        User owner = new User();
        owner.setUsername(username);
        Card card = new Card();
        card.setOwner(owner);
        card.setStatus(status);
        card.setBalance(new BigDecimal(balance));
        card.setNumberLast4("1234");
        card.setExpiresAt(LocalDate.now().plusMonths(6));
        return card;
    }
}
