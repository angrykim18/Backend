package com.newez.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vodfile") // 실제 DB 테이블 이름이 다르다면 이 부분을 수정해야 합니다.
@Getter
@Setter
public class VodFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDX")
    private Long id;

    @Column(name = "VODFILENUMBER")
    private String vodFileNumber;

    @Column(name = "VODFILENAME")
    private String vodFileName;

    @Column(name = "VODFILEPATH")
    private String vodFilePath;

    @Column(name = "VODIDX")
    private Long vodContentId; // 어떤 VOD 콘텐츠에 속하는지 연결하는 ID
}