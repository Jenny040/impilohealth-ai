package com.impilohealth.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected User() {
    }

    public User(String fullName, String email, String passwordHash, Gender gender, LocalDate dateOfBirth) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Gender getGender() { return gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Role getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }

    public void setRole(Role role) { this.role = role; }
}
