package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardDtos {
    public record CreateCardRequest(
            @NotNull Long ownerId,
            @NotBlank String number,
            @NotNull LocalDate expiresAt,
            @NotNull @DecimalMin("0.00") BigDecimal initialBalance
    ) {}

    public record UpdateCardStatusRequest(@NotNull CardStatus status) {}

    public record CardResponse(
            Long id,
            String maskedNumber,
            String owner,
            CardStatus status,
            BigDecimal balance,
            LocalDate expiresAt
    ) {}
}
