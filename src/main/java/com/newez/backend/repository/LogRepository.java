package com.newez.backend.repository;

import com.newez.backend.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface LogRepository extends JpaRepository<Log, Long> {
}