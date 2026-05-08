package com.example.bankcards.service;

import com.example.bankcards.dto.UserDtos;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserSuccess() {
        UserDtos.CreateUserRequest request = new UserDtos.CreateUserRequest("alice", "secret", Role.ROLE_USER);
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User saved = Objects.requireNonNull(invocation.getArgument(0));
            saved.setId(100L);
            return saved;
        });

        UserDtos.UserResponse response = userService.create(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = Objects.requireNonNull(captor.getValue());
        assertEquals("alice", saved.getUsername());
        assertEquals("hashed", saved.getPasswordHash());
        assertEquals(Role.ROLE_USER, saved.getRole());
        assertEquals(100L, response.id());
        assertEquals("alice", response.username());
        assertEquals(Role.ROLE_USER, response.role());
    }

    @Test
    void createUserFailsWhenUsernameExists() {
        UserDtos.CreateUserRequest request = new UserDtos.CreateUserRequest("alice", "secret", Role.ROLE_USER);
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.create(request));
    }

    @Test
    void findByUsernameFailsWhenNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.findByUsername("missing"));
    }
}
