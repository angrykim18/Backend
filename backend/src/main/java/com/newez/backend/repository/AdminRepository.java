package com.newez.backend.repository;

import com.newez.backend.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    // 로그인 ID로 관리자 정보를 찾는 기능
    Optional<Admin> findByAdminId(String adminId);
}