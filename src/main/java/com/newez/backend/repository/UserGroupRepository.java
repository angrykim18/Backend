package com.newez.backend.repository;

import com.newez.backend.domain.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Optional import 추가

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    // ✅ 그룹 이름으로 그룹 정보를 찾는 이 기능이 빠져있었습니다.
    Optional<UserGroup> findByGroupName(String groupName);

}