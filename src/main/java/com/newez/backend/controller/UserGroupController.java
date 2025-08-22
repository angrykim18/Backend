package com.newez.backend.controller;

import com.newez.backend.domain.UserGroup;
import com.newez.backend.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-groups")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class UserGroupController {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @GetMapping
    public List<UserGroup> getAllGroups() {
        return userGroupRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGroup> getGroupById(@PathVariable Long id) {
        return userGroupRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserGroup createGroup(@RequestBody UserGroup group) {
        return userGroupRepository.save(group);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGroup> updateGroup(@PathVariable Long id, @RequestBody UserGroup groupDetails) {
        return userGroupRepository.findById(id)
                .map(group -> {
                    group.setGroupName(groupDetails.getGroupName());
                    group.setDescription(groupDetails.getDescription());
                    // ✅ 사라졌던 서버 ID 저장 로직을 다시 추가합니다.
                    group.setLiveServerId(groupDetails.getLiveServerId());
                    group.setVodServerId(groupDetails.getVodServerId());
                    group.setReplayServerId(groupDetails.getReplayServerId());
                    UserGroup updatedGroup = userGroupRepository.save(group);
                    return ResponseEntity.ok(updatedGroup);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        userGroupRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}