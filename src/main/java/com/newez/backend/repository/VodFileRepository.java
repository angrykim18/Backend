package com.newez.backend.repository;

import com.newez.backend.domain.VodFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * VOD 파일 데이터베이스 관리를 위한 JpaRepository 인터페이스입니다.
 */
public interface VodFileRepository extends JpaRepository<VodFile, Long> {

    /**
     * 특정 콘텐츠에 속한 파일 목록을 '순서 번호'의 역순(내림차순)으로 정렬하여
     * 페이지 단위로 조회합니다.
     * @param vodContentId VOD 콘텐츠의 ID
     * @param pageable 페이지 정보
     * @return 정렬된 VodFile 페이지 객체
     */
    @Query("SELECT v FROM VodFile v WHERE v.vodContentId = :vodContentId ORDER BY v.fileOrder DESC")
    Page<VodFile> findByVodContentId(@Param("vodContentId") Long vodContentId, Pageable pageable);

    /**
     * 특정 콘텐츠에 속한 모든 파일 목록을 '순서 번호'의 역순(내림차순)으로 정렬하여
     * 리스트 형태로 조회합니다.
     * @param vodContentId VOD 콘텐츠의 ID
     * @return 정렬된 VodFile 리스트
     */
    List<VodFile> findAllByVodContentIdOrderByFileOrderDesc(Long vodContentId);

    /**
     * 특정 콘텐츠에 속한 파일들 중 가장 큰 '순서 번호'를 조회합니다.
     * 새 파일 등록 시 다음 번호를 부여하기 위해 사용됩니다.
     * @param vodContentId VOD 콘텐츠의 ID
     * @return 가장 큰 순서 번호 (Optional)
     */
    @Query("SELECT MAX(v.fileOrder) FROM VodFile v WHERE v.vodContentId = :vodContentId")
    Optional<Integer> findMaxFileOrderByVodContentId(@Param("vodContentId") Long vodContentId);
}