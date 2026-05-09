package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDtos;
import com.example.bankcards.dto.UserDtos;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final CardService cardService;
    private final UserService userService;

    public AdminController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @PostMapping("/cards")
    public CardDtos.CardResponse createCard(@RequestBody @Valid CardDtos.CreateCardRequest request) {
        return cardService.create(request);
    }

    @PatchMapping("/cards/{id}/status")
    public CardDtos.CardResponse updateCardStatus(@PathVariable @NonNull Long id, @RequestBody @Valid CardDtos.UpdateCardStatusRequest request) {
        return cardService.updateStatus(id, request.status());
    }

    @DeleteMapping("/cards/{id}")
    public void deleteCard(@PathVariable @NonNull Long id) {
        cardService.delete(id);
    }

    @GetMapping("/cards")
    public Page<CardDtos.CardResponse> allCards(@RequestParam(required = false) String search, Pageable pageable) {
        return cardService.allCards(search, pageable);
    }

    @GetMapping("/cards/{id}")
    public CardDtos.CardResponse cardById(@PathVariable @NonNull Long id) {
        return cardService.cardById(id);
    }

    @PostMapping("/users")
    public UserDtos.UserResponse createUser(@RequestBody @Valid UserDtos.CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/users")
    public List<UserDtos.UserResponse> users() {
        return userService.all();
    }
}
