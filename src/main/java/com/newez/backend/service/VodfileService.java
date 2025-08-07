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

import java.util.ArrayList; // ✅ [추가]
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
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public VodfileService(VodFileRepository vodFileRepository, ServerRepository serverRepository,
                          VodContentRepository vodContentRepository, UserRepository userRepository,
                          UserGroupRepository userGroupRepository) {
        this.vodFileRepository = vodFileRepository;
        this.serverRepository = serverRepository;
        this.vodContentRepository = vodContentRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
    }

    public Page<VodFileResponseDto> getFilesByContentId(Long contentId, String deviceId, Pageable pageable) {
        final String serverUrl = getVodServerUrlForDevice(deviceId);
        final String SUFFIX = "/playlist.m3u8";

        Page<VodFile> episodesPage = vodFileRepository.findByVodContentId(contentId, pageable);
        return episodesPage.map(episode -> {
            String fullUrl = serverUrl + episode.getVodFilePath() + SUFFIX;
            return new VodFileResponseDto(episode, fullUrl);
        });
    }

    public List<VodFileResponseDto> getAllFilesByContentId(Long contentId, String deviceId) {
        final String serverUrl = getVodServerUrlForDevice(deviceId);
        final String SUFFIX = "/playlist.m3u8";

        List<VodFile> allFiles = vodFileRepository.findAllByVodContentIdOrderByFileOrderDesc(contentId);
        return allFiles.stream().map(episode -> {
            String fullUrl = serverUrl + episode.getVodFileName() + SUFFIX;
            return new VodFileResponseDto(episode, fullUrl);
        }).collect(Collectors.toList());
    }

    private String getVodServerUrlForDevice(String deviceId) {
        if (deviceId != null && !deviceId.isEmpty()) {
            String url = userRepository.findByDeviceId(deviceId)
                    .flatMap(user -> userGroupRepository.findByGroupName(user.getUserGroup()))
                    .flatMap(userGroup -> serverRepository.findById(userGroup.getVodServerId()))
                    .map(Server::getServerUrl)
                    .orElse(null);

            if (url != null) {
                return url;
            }
        }
        return serverRepository.findAll().stream().findFirst()
                .map(Server::getServerUrl).orElse("");
    }

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

    // ✅ [수정] 비효율적인 반복 저장을 한 번의 'saveAll' 호출로 변경하여 성능과 안정성을 개선합니다.
    @Transactional
    public void reorderFiles(Long contentId, List<Map<String, Object>> filesPayload) {
        // 프론트엔드에서 이미 검증하지만, 안전을 위해 서버에서도 중복을 한번 더 체크합니다.
        Set<Integer> orderSet = new HashSet<>();
        for (Map<String, Object> item : filesPayload) {
            Object orderObj = item.get("order");
            if (orderObj == null || !orderSet.add(Integer.valueOf(orderObj.toString()))) {
                throw new IllegalStateException("중복되거나 비어있는 순서 번호가 있습니다.");
            }
        }

        // 업데이트가 필요한 파일들을 담을 리스트를 생성합니다.
        List<VodFile> filesToUpdate = new ArrayList<>();
        for (Map<String, Object> item : filesPayload) {
            Long id = Long.valueOf(item.get("id").toString());
            Integer order = Integer.valueOf(item.get("order").toString());

            // ID로 파일을 찾아서, 순서를 업데이트하고 리스트에 추가합니다.
            vodFileRepository.findById(id).ifPresent(file -> {
                file.setFileOrder(order);
                filesToUpdate.add(file);
            });
        }

        // 리스트에 담긴 모든 파일의 변경사항을 데이터베이스에 한 번에 저장합니다.
        vodFileRepository.saveAll(filesToUpdate);

        // 부모 콘텐츠의 최종 수정 시간을 업데이트합니다.
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