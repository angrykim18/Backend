package com.newez.backend.repository;

import com.newez.backend.domain.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency, Long> {

    Optional<Agency> findByLoginId(String loginId);
}