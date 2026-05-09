package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private TransferService transferService;

    @Test
    void transferSuccess() {
        Card from = card("user", "100.00");
        Card to = card("user", "20.00");
        when(cardService.getCard(1L)).thenReturn(from);
        when(cardService.getCard(2L)).thenReturn(to);

        transferService.transfer(1L, 2L, new BigDecimal("30.00"), "user");

        assertEquals(new BigDecimal("70.00"), from.getBalance());
        assertEquals(new BigDecimal("50.00"), to.getBalance());
    }

    @Test
    void transferFailsForNotOwner() {
        Card from = card("user", "100.00");
        Card to = card("other", "20.00");
        when(cardService.getCard(1L)).thenReturn(from);
        when(cardService.getCard(2L)).thenReturn(to);

        assertThrows(BusinessException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("30.00"), "user"));
    }

    @Test
    void transferFailsForNonPositiveAmount() {
        assertThrows(BusinessException.class,
                () -> transferService.transfer(1L, 2L, BigDecimal.ZERO, "user"));
    }

    @Test
    void transferFailsWhenCardNotActive() {
        Card from = card("user", "100.00");
        Card to = card("user", "20.00");
        from.setStatus(CardStatus.BLOCKED);
        when(cardService.getCard(1L)).thenReturn(from);
        when(cardService.getCard(2L)).thenReturn(to);

        assertThrows(BusinessException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("30.00"), "user"));
    }

    @Test
    void transferFailsForInsufficientFunds() {
        Card from = card("user", "10.00");
        Card to = card("user", "20.00");
        when(cardService.getCard(1L)).thenReturn(from);
        when(cardService.getCard(2L)).thenReturn(to);

        assertThrows(BusinessException.class,
                () -> transferService.transfer(1L, 2L, new BigDecimal("30.00"), "user"));
    }

    private Card card(String username, String balance) {
        User user = new User();
        user.setUsername(username);
        Card card = new Card();
        card.setOwner(user);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(new BigDecimal(balance));
        return card;
    }
}
