package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vodfile")
@Getter
@Setter
public class VodFile extends BaseTimeEntity { // ✅ [수정] BaseTimeEntity 상속

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long id;

    @Column(name = "VODFILENUMBER")
    private String vodFileNumber; // ✅ [명칭 변경] 안드로이드 노출용 이름

    @Column(name = "VODFILENAME")
    private String vodFileName; // ✅ [명칭 변경] 실제 파일 이름 (.mp4 포함)

    @Column(name = "VODFILEPATH")
    private String vodFilePath;

    @Column(name = "VODIDX")
    private Long vodContentId;

    @Column(name = "FILEORDER") // ✅ [추가] 순서 저장을 위한 필드
    private Integer fileOrder;
}