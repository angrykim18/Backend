package com.newez.backend.service;

import com.newez.backend.domain.Server;
import com.newez.backend.domain.User;
import com.newez.backend.domain.UserGroup;
import com.newez.backend.domain.VodFile;
import com.newez.backend.dto.VodFileResponseDto;
import com.newez.backend.dto.VodFileUpdateDto;
import com.newez.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VodfileService {

    private final VodFileRepository vodFileRepository;
    private final ServerRepository serverRepository;
    private final VodContentRepository vodContentRepository;
    private final UserRepository userRepository; // ✅ [추가]
    private final UserGroupRepository userGroupRepository; // ✅ [추가]

    @Autowired
    public VodfileService(VodFileRepository vodFileRepository, ServerRepository serverRepository,
                          VodContentRepository vodContentRepository, UserRepository userRepository,
                          UserGroupRepository userGroupRepository) {
        this.vodFileRepository = vodFileRepository;
        this.serverRepository = serverRepository;
        this.vodContentRepository = vodContentRepository;
        this.userRepository = userRepository; // ✅ [추가]
        this.userGroupRepository = userGroupRepository; // ✅ [추가]
    }

    // ✅ [수정] VOD URL 조합 로직 전체 변경
    public Page<VodFileResponseDto> getFilesByContentId(Long contentId, String deviceId, Pageable pageable) {
        final String serverUrl = getVodServerUrlForDevice(deviceId);
        final String SUFFIX = "/playlist.m3u8";

        Page<VodFile> episodesPage = vodFileRepository.findByVodContentId(contentId, pageable);
        return episodesPage.map(episode -> {
            String fullUrl = serverUrl + episode.getVodFilePath() + SUFFIX;
            return new VodFileResponseDto(episode, fullUrl);
        });
    }

    // ✅ [수정] VOD URL 조합 로직 전체 변경
    public List<VodFileResponseDto> getAllFilesByContentId(Long contentId, String deviceId) {
        final String serverUrl = getVodServerUrlForDevice(deviceId);
        final String SUFFIX = "/playlist.m3u8";

        List<VodFile> allFiles = vodFileRepository.findAllByVodContentIdOrderByFileOrderDesc(contentId);
        return allFiles.stream().map(episode -> {
            String fullUrl = serverUrl + episode.getVodFileName() + SUFFIX;
            return new VodFileResponseDto(episode, fullUrl);
        }).collect(Collectors.toList());
    }

    // ✅ [추가] deviceId를 기반으로 VOD 서버 URL을 찾는 헬퍼 메소드
    private String getVodServerUrlForDevice(String deviceId) {
        if (deviceId != null && !deviceId.isEmpty()) {
            // Optional을 사용하여 null-safe하게 처리합니다.
            String url = userRepository.findByDeviceId(deviceId)
                    .flatMap(user -> userGroupRepository.findByGroupName(user.getUserGroup()))
                    .flatMap(userGroup -> serverRepository.findById(userGroup.getVodServerId()))
                    .map(Server::getServerUrl)
                    .orElse(null); // 사용자를 못찾거나, 그룹/서버가 없으면 null 반환

            if (url != null) {
                return url;
            }
        }
        // deviceId가 없거나(웹 요청) 위 과정에서 URL을 찾지 못한 경우, DB의 첫 번째 서버를 기본값으로 사용합니다.
        return serverRepository.findAll().stream().findFirst()
                .map(Server::getServerUrl).orElse("");
    }


    // --- 이하 다른 메소드들은 수정되지 않았습니다 ---

    @Transactional
    public VodFile saveFileInfo(VodFile vodFile) {
        Integer maxOrder = vodFileRepository.findMaxFileOrderByVodContentId(vodFile.getVodContentId()).orElse(0);
        vodFile.setFileOrder(maxOrder + 1);
        VodFile savedFile = vodFileRepository.save(vodFile);
        updateParentContentTimestamp(savedFile.getVodContentId());
        return savedFile;
    }

    @Transactional
    public VodFile updateVodFile(Long id, VodFileUpdateDto fileDetails) {
        VodFile vodFile = vodFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + id));
        vodFile.setVodFileName(fileDetails.getVodFileName());
        vodFile.setVodFilePath(fileDetails.getVodFilePath());
        VodFile updatedFile = vodFileRepository.save(vodFile);
        updateParentContentTimestamp(updatedFile.getVodContentId());
        return updatedFile;
    }

    @Transactional
    public void reorderFiles(Long contentId, List<Map<String, Object>> filesPayload) {
        Set<Integer> orderSet = new HashSet<>();
        for (Map<String, Object> item : filesPayload) {
            Integer order = (Integer) item.get("order");
            if (order == null || !orderSet.add(order)) {
                throw new IllegalStateException("중복된 순서 번호(" + order + ")가 있거나 비어있는 번호가 있습니다. 다시 확인해주세요.");
            }
        }
        for (Map<String, Object> item : filesPayload) {
            Long id = Long.valueOf(item.get("id").toString());
            Integer order = (Integer) item.get("order");
            vodFileRepository.findById(id).ifPresent(file -> {
                file.setFileOrder(order);
                vodFileRepository.save(file);
            });
        }
        updateParentContentTimestamp(contentId);
    }

    @Transactional
    public void deleteVodFile(Long id) {
        VodFile vodFileToDelete = vodFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("삭제할 파일을 찾을 수 없습니다: " + id));
        Long contentId = vodFileToDelete.getVodContentId();
        vodFileRepository.delete(vodFileToDelete);
        updateParentContentTimestamp(contentId);
    }

    @Transactional
    public void moveVodFile(Long id, Long newContentId) {
        VodFile vodFile = vodFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다: " + id));
        Long originalContentId = vodFile.getVodContentId();
        vodFile.setVodContentId(newContentId);
        vodFileRepository.save(vodFile);
        if (originalContentId != null) {
            updateParentContentTimestamp(originalContentId);
        }
        updateParentContentTimestamp(newContentId);
    }

    private void updateParentContentTimestamp(Long contentId) {
        if (contentId == null) return;
        vodContentRepository.updateTimestamp(contentId);
    }
}