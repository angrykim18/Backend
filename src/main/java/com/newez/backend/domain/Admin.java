package com.newez.backend.domain;
import jakarta.persistence.*;
import lombok.Getter;
@Entity @Table(name = "admins") @Getter
public class Admin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "admin_id", unique = true, nullable = false)
    private String adminId;
    @Column(nullable = false)
    private String password;
    private String name;
}