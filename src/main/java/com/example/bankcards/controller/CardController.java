package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDtos;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    private final CardService cardService;
    private final UserService userService;

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @GetMapping
    public Page<CardDtos.CardResponse> myCards(@RequestParam(required = false) String search,
                                               Pageable pageable,
                                               Authentication authentication) {
        return cardService.ownCards(userService.findByUsername(authentication.getName()), search, pageable);
    }

    @GetMapping("/{id}")
    public CardDtos.CardResponse myCard(@PathVariable @NonNull Long id, Authentication authentication) {
        return cardService.ownCardById(id, authentication.getName());
    }

    @PatchMapping("/{id}/request-block")
    public CardDtos.CardResponse requestBlock(@PathVariable @NonNull Long id, Authentication authentication) {
        return cardService.requestBlock(id, authentication.getName());
    }
}
