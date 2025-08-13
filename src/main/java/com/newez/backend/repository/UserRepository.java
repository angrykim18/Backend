package com.newez.backend.repository;

import com.newez.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByDeviceId(String deviceId);



    @Query("SELECT u FROM User u WHERE " +
            "u.deviceId LIKE %:keyword% OR " +
            "u.managementId LIKE %:keyword% OR " +
            "u.name LIKE %:keyword% OR " +
            "u.memo LIKE %:keyword%")
    Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ✅ [핵심 버그 수정] agency_group_mappings 테이블을 사용하는 올바른 쿼리로 수정했습니다.
    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_groups g ON u.user_group = g.group_name " +
            "JOIN agency_group_mappings m ON g.id = m.group_id " +
            "WHERE m.agency_id = :agencyId",
            countQuery = "SELECT count(*) FROM users u " +
                    "JOIN user_groups g ON u.user_group = g.group_name " +
                    "JOIN agency_group_mappings m ON g.id = m.group_id " +
                    "WHERE m.agency_id = :agencyId",
            nativeQuery = true)
    Page<User> findByAgencyId(@Param("agencyId") Long agencyId, Pageable pageable);

    Page<User> findAllByOrderByIdDesc(Pageable pageable);
}