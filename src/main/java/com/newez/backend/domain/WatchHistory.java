package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "watch_history")
@Getter
@Setter
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User 테이블과의 관계 설정 (어떤 사용자의 기록인지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // VodFile 테이블과의 관계 설정 (어떤 에피소드의 기록인지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vod_file_id")
    private VodFile vodFile;

    @Column(name = "timestamp_seconds")
    private int timestampSeconds;

    @UpdateTimestamp // 레코드가 업데이트될 때마다 자동으로 현재 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}