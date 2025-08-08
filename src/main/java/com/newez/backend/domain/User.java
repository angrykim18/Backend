package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.Date;
import lombok.Setter;

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
    private Date subscriptionEndDate;

    @Column(name = "adult_content_allowed")
    private boolean adultContentAllowed;

    private String memo;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}