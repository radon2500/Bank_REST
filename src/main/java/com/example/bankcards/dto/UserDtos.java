package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserDtos {
    public record CreateUserRequest(@NotBlank String username, @NotBlank String password, @NotNull Role role) {}
    public record UserResponse(Long id, String username, Role role) {}
}
