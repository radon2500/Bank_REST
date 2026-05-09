package com.example.bankcards.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "encrypted_number", nullable = false, unique = true, length = 512)
    private String encryptedNumber;

    @Column(name = "number_last4", nullable = false, length = 4)
    private String numberLast4;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "expires_at", nullable = false)
    private LocalDate expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public String getEncryptedNumber() { return encryptedNumber; }
    public String getNumberLast4() { return numberLast4; }
    public User getOwner() { return owner; }
    public CardStatus getStatus() { return status; }
    public BigDecimal getBalance() { return balance; }
    public LocalDate getExpiresAt() { return expiresAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setEncryptedNumber(String encryptedNumber) { this.encryptedNumber = encryptedNumber; }
    public void setNumberLast4(String numberLast4) { this.numberLast4 = numberLast4; }
    public void setOwner(User owner) { this.owner = owner; }
    public void setStatus(CardStatus status) { this.status = status; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
