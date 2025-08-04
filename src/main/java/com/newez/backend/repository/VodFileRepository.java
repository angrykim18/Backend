package com.newez.backend.repository;

import com.newez.backend.domain.VodFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface VodFileRepository extends JpaRepository<VodFile, Long> {


    @Query("SELECT v FROM VodFile v WHERE v.vodContentId = :vodContentId ORDER BY v.id DESC")
    Page<VodFile> findByVodContentId(@Param("vodContentId") Long vodContentId, Pageable pageable);


    List<VodFile> findAllByVodContentIdOrderByFileOrderDesc(Long vodContentId);


    @Query("SELECT MAX(v.fileOrder) FROM VodFile v WHERE v.vodContentId = :vodContentId")
    Optional<Integer> findMaxFileOrderByVodContentId(@Param("vodContentId") Long vodContentId);
}