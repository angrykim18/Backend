package com.newez.backend.repository;

import com.newez.backend.domain.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    // ✅ 로그인 ID로 대리점을 찾을 때, 결과가 없을 수도 있으므로 Optional로 감싸서 반환합니다.
    Optional<Agency> findByLoginId(String loginId);
}