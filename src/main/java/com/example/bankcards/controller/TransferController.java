package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public void transfer(@RequestBody @Valid TransferRequest request, Authentication authentication) {
        transferService.transfer(
                Objects.requireNonNull(request.fromCardId(), "fromCardId must not be null"),
                Objects.requireNonNull(request.toCardId(), "toCardId must not be null"),
                request.amount(),
                authentication.getName()
        );
    }
}
