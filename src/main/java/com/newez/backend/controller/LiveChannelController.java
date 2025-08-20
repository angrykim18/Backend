package com.newez.backend.controller;

import com.newez.backend.domain.LiveChannel;
import com.newez.backend.domain.Server;
import com.newez.backend.domain.User;
import com.newez.backend.domain.UserGroup;
import com.newez.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/live-channels")
@CrossOrigin(origins = "${cors.allowed-origins}")
@RequiredArgsConstructor
public class LiveChannelController {

    // ✅ @Autowired를 제거하고 private final로 변경하여 생성자 주입 방식을 사용합니다.
    private final LiveChannelRepository liveChannelRepository;
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    // 웹 관리자 페이지용 API (페이지네이션)
    @GetMapping(params = "page")
    public Page<LiveChannel> getAllLiveChannelsPaginated(Pageable pageable) {
        return liveChannelRepository.findAllByOrderByDisplayOrderAsc(pageable);
    }

    // 안드로이드 앱용 API (사용자 그룹별 서버 매칭 적용)
    @GetMapping
    public List<LiveChannel> getAllLiveChannelsForApp(@RequestParam String deviceId) {
        // 1. deviceId로 사용자 정보를 찾습니다.
        Optional<User> userOptional = userRepository.findByDeviceId(deviceId);
        if (userOptional.isEmpty()) {
            return Collections.emptyList(); // 사용자가 없으면 빈 목록 반환
        }
        User user = userOptional.get();

        // 2. 사용자의 그룹 이름을 가져옵니다.
        String groupName = user.getUserGroup();
        if (groupName == null || groupName.isEmpty()) {
            return Collections.emptyList(); // 그룹이 없으면 빈 목록 반환
        }

        // 3. 그룹 이름으로 그룹 정보를 찾습니다.
        Optional<UserGroup> userGroupOptional = userGroupRepository.findByGroupName(groupName);
        if (userGroupOptional.isEmpty()) {
            return Collections.emptyList(); // 해당하는 그룹 정보가 없으면 빈 목록 반환
        }
        UserGroup userGroup = userGroupOptional.get();

        // 4. 그룹 정보에서 생방송 서버 ID를 가져옵니다.
        Long liveServerId = userGroup.getLiveServerId();
        if (liveServerId == null) {
            return Collections.emptyList(); // 그룹에 생방송 서버가 지정되지 않았으면 빈 목록 반환
        }

        // 5. 서버 ID로 실제 서버 정보를 찾습니다.
        Optional<Server> serverOptional = serverRepository.findById(liveServerId);
        if (serverOptional.isEmpty()) {
            return Collections.emptyList(); // 해당하는 서버 정보가 없으면 빈 목록 반환
        }
        String serverUrl = serverOptional.get().getServerUrl();

        // 6. 찾은 서버 URL을 모든 채널의 streamUrl에 붙여서 최종 목록을 만듭니다.
        List<LiveChannel> channels = liveChannelRepository.findAllByOrderByDisplayOrderAsc();
        channels.forEach(channel ->
                channel.setStreamUrl(serverUrl + channel.getStreamUrl())
        );
        return channels;
    }

    // --- 이하 다른 메소드들은 모두 이전과 동일합니다 ---

    @GetMapping("/{id}")
    public ResponseEntity<LiveChannel> getLiveChannelById(@PathVariable Long id) {
        return liveChannelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public LiveChannel createLiveChannel(@RequestBody LiveChannel liveChannel) {
        // 새 채널 저장 시에는 URL 조합 로직이 필요 없으므로 그대로 둡니다.
        return liveChannelRepository.save(liveChannel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LiveChannel> updateLiveChannel(@PathVariable Long id, @RequestBody LiveChannel channelDetails) {
        return liveChannelRepository.findById(id)
                .map(channel -> {
                    channel.setDisplayOrder(channelDetails.getDisplayOrder());
                    channel.setChannelName(channelDetails.getChannelName());
                    channel.setChannelTitle(channelDetails.getChannelTitle());
                    channel.setStreamUrl(channelDetails.getStreamUrl());
                    LiveChannel updatedChannel = liveChannelRepository.save(channel);
                    return ResponseEntity.ok(updatedChannel);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLiveChannel(@PathVariable Long id) {
        return liveChannelRepository.findById(id)
                .map(channel -> {
                    liveChannelRepository.delete(channel);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}