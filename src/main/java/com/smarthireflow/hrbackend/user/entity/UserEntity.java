package com.smarthireflow.hrbackend.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.smarthireflow.hrbackend.user.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String email;

    @JsonIgnore
    @Column(name="password_hash", nullable=false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=32)
    private Role role;

    @Column(name="full_name")
    private String fullName;

    // Optional profile fields
    private String department;
    private String phone;

    @Column(length = 512)
    private String address;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    @JsonIgnore
    private String avatarContentType;

    // 2FA fields
    @Column(name = "two_factor_enabled")
    private boolean twoFactorEnabled;

    @JsonIgnore
    @Column(name = "two_factor_secret")
    private String twoFactorSecret;
}