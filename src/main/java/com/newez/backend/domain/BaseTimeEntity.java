package com.newez.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 Entity의 공통 필드인 생성시간, 수정시간을 자동으로 관리하는 클래스입니다.
 */
@Getter
@MappedSuperclass // 이 클래스를 상속받는 Entity들은 아래 필드들을 컬럼으로 인식하게 됩니다.
@EntityListeners(AuditingEntityListener.class) // 시간 자동 기록 기능을 추가합니다.
public abstract class BaseTimeEntity {

    @CreatedDate // Entity가 생성될 때 시간이 자동 저장됩니다.
    @Column(updatable = false) // 생성 시간은 수정되지 않도록 설정합니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // Entity가 수정될 때마다 시간이 자동 저장됩니다.
    private LocalDateTime updatedAt;
}