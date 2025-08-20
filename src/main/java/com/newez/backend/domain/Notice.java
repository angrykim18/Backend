package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "notices")
@Getter
@Setter
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ [추가] '일반', '긴급' 등 공지 타입을 저장하는 필드
    @Column(name = "notice_type")
    private String noticeType;

    private String content;

    @Column(name = "target_group")
    private String targetGroup;

    @Column(name = "is_active")
    private boolean isActive;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
}