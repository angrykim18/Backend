package com.newez.backend.repository;

import com.newez.backend.domain.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Optional import 추가

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {


    Optional<UserGroup> findByGroupName(String groupName);

}