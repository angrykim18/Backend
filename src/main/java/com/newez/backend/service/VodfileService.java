package com.newez.backend.service;

import com.newez.backend.domain.Server;
import com.newez.backend.domain.VodContent;
import com.newez.backend.domain.VodFile;
import com.newez.backend.dto.VodFileResponseDto;
import com.newez.backend.dto.VodFileUpdateDto;
import com.newez.backend.repository.ServerRepository;
import com.newez.backend.repository.VodContentRepository;
import com.newez.backend.repository.VodFileRepository;
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

    @Autowired
    public VodfileService(VodFileRepository vodFileRepository, ServerRepository serverRepository, VodContentRepository vodContentRepository) {
        this.vodFileRepository = vodFileRepository;
        this.serverRepository = serverRepository;
        this.vodContentRepository = vodContentRepository;
    }

    public Page<VodFileResponseDto> getFilesByContentId(Long contentId, Pageable pageable) {
        List<Server> servers = serverRepository.findAll();
        String serverUrl = servers.isEmpty() ? "" : servers.get(0).getServerUrl();
        Page<VodFile> episodesPage = vodFileRepository.findByVodContentId(contentId, pageable);
        return episodesPage.map(episode -> {
            String fullUrl = "";
            if (episode.getVodFilePath() != null && episode.getVodFileName() != null) {
                fullUrl = serverUrl + episode.getVodFilePath() + "/" + episode.getVodFileName();
            }
            return new VodFileResponseDto(episode, fullUrl);
        });
    }

    public List<VodFileResponseDto> getAllFilesByContentId(Long contentId) {
        List<Server> servers = serverRepository.findAll();
        String serverUrl = servers.isEmpty() ? "" : servers.get(0).getServerUrl();
        List<VodFile> allFiles = vodFileRepository.findAllByVodContentIdOrderByFileOrderDesc(contentId);
        return allFiles.stream().map(episode -> {
            String fullUrl = "";
            if (episode.getVodFilePath() != null && episode.getVodFileName() != null) {
                fullUrl = serverUrl + episode.getVodFilePath() + "/" + episode.getVodFileName();
            }
            return new VodFileResponseDto(episode, fullUrl);
        }).collect(Collectors.toList());
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

        // ✅ [최종 수정] 원래 부모와 새 부모의 수정 시간을 모두 갱신
        Long originalContentId = vodFile.getVodContentId();

        vodFile.setVodContentId(newContentId);
        vodFileRepository.save(vodFile);

        if (originalContentId != null) {
            updateParentContentTimestamp(originalContentId);
        }
        updateParentContentTimestamp(newContentId);
    }

    /**
     * 부모 콘텐츠의 수정 시간을 갱신하는 공통 메소드
     */
    private void updateParentContentTimestamp(Long contentId) {
        if (contentId == null) return;
        // ✅ [최종 수정] Repository에 추가한 쿼리를 직접 호출하여 시간을 강제로 갱신
        vodContentRepository.updateTimestamp(contentId);
    }
}