package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "vod_contents")
@Getter
@Setter
public class VodContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 컨텐츠 제목

    @Lob // 긴 텍스트를 위한 설정
    @Column(columnDefinition = "TEXT")
    private String description; // 컨텐츠 소개 메모

    @Column(nullable = false)
    private boolean exposed = true; // 노출 여부 (기본값 true)

    @CreationTimestamp // 데이터 생성 시 자동으로 날짜/시간 기록
    private LocalDateTime createdAt;

    private String posterPath; // 포스터 파일 경로

    // 이 콘텐츠가 어떤 카테고리에 속하는지 연결하는 ID
    private Long categoryId;
}