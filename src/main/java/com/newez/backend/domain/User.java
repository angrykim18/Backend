package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.Date;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", unique = true, nullable = false, length = 512)
    private String deviceId;

    @Column(name = "management_id", unique = true, nullable = false)
    private String managementId;

    private String name;

    @Column(name = "user_group")
    private String userGroup;

    private String status;

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    @Column(name = "adult_content_allowed")
    private boolean adultContentAllowed;

    private String memo;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    // ▼▼▼ 여기에 새 필드를 추가합니다 ▼▼▼

    @Column(length = 45)
    private String ip;

    @Column(length = 50)
    private String country;
}