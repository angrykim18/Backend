package com.newez.backend.controller;

import com.newez.backend.domain.LiveChannel;
import com.newez.backend.domain.Server; // ✅ import 추가
import com.newez.backend.repository.LiveChannelRepository;
import com.newez.backend.repository.ServerRepository; // ✅ import 추가
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/live-channels")
@CrossOrigin(origins = "http://localhost:3000")
public class LiveChannelController {

    @Autowired
    private LiveChannelRepository liveChannelRepository;

    // ✅ [의존성 추가] ServerRepository를 주입받습니다.
    @Autowired
    private ServerRepository serverRepository;

    // ✅ [웹용] 페이지네이션 요청을 처리
    @GetMapping(params = "page")
    public Page<LiveChannel> getAllLiveChannelsPaginated(Pageable pageable) {
        // ✅ [로직 추가] 서버 URL을 가져옵니다. (첫 번째 서버를 기준으로 함)
        List<Server> servers = serverRepository.findAll();
        if (servers.isEmpty()) {
            // 서버 정보가 없으면 빈 페이지 반환
            return Page.empty();
        }
        String serverUrl = servers.get(0).getServerUrl();

        // ✅ [로직 추가] 채널 목록을 가져와 각 채널의 streamUrl을 완성된 주소로 변경
        Page<LiveChannel> channelsPage = liveChannelRepository.findAllByOrderByDisplayOrderAsc(pageable);
        channelsPage.getContent().forEach(channel ->
                channel.setStreamUrl(serverUrl + channel.getStreamUrl())
        );

        return channelsPage;
    }

    // ✅ [안드로이드용] 전체 목록 요청을 처리
    @GetMapping
    public List<LiveChannel> getAllLiveChannels() {
        // ✅ [로직 추가] 서버 URL을 가져옵니다. (첫 번째 서버를 기준으로 함)
        List<Server> servers = serverRepository.findAll();
        if (servers.isEmpty()) {
            // 서버 정보가 없으면 빈 리스트 반환
            return List.of();
        }
        String serverUrl = servers.get(0).getServerUrl();

        // ✅ [로직 추가] 채널 목록을 가져와 각 채널의 streamUrl을 완성된 주소로 변경
        List<LiveChannel> channels = liveChannelRepository.findAllByOrderByDisplayOrderAsc();
        channels.forEach(channel ->
                channel.setStreamUrl(serverUrl + channel.getStreamUrl())
        );

        return channels;
    }

    // --- 이하 다른 메소드들은 그대로 유지 ---

    @GetMapping("/{id}")
    public ResponseEntity<LiveChannel> getLiveChannelById(@PathVariable Long id) {
        return liveChannelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public LiveChannel createLiveChannel(@RequestBody LiveChannel liveChannel) {
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