package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
// import org.hibernate.annotations.CreationTimestamp; // 더 이상 필요 없으므로 삭제
import java.time.LocalDateTime;

@Entity
@Table(name = "vod_contents")
@Getter
@Setter
public class VodContent extends BaseTimeEntity { // ✅ [수정] BaseTimeEntity를 상속받습니다.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 컨텐츠 제목

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description; // 컨텐츠 소개 메모

    @Column(nullable = false)
    private boolean exposed = true; // 노출 여부 (기본값 true)



    private String posterPath; // 포스터 파일 경로

    private Long categoryId;
}