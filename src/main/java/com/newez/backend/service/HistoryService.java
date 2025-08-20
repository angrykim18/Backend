package com.newez.backend.service;

import com.newez.backend.domain.User;
import com.newez.backend.domain.VodFile;
import com.newez.backend.domain.WatchHistory;
import com.newez.backend.dto.WatchHistoryDto;
import com.newez.backend.repository.UserRepository;
import com.newez.backend.repository.VodFileRepository;
import com.newez.backend.repository.WatchHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoryService {

    @Autowired
    private WatchHistoryRepository watchHistoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VodFileRepository vodFileRepository;

    @Transactional
    public void saveOrUpdateHistory(WatchHistoryDto historyDto) {
        User user = userRepository.findByDeviceId(historyDto.getDeviceId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with deviceId: " + historyDto.getDeviceId()));

        VodFile vodFile = vodFileRepository.findById(historyDto.getVodFileId())
                .orElseThrow(() -> new EntityNotFoundException("VodFile not found with id: " + historyDto.getVodFileId()));

        WatchHistory history = watchHistoryRepository.findByUserAndVodFile(user, vodFile)
                .orElse(new WatchHistory()); // 기존 기록이 없으면 새로 생성

        history.setUser(user);
        history.setVodFile(vodFile);
        history.setTimestampSeconds(historyDto.getTimestampSeconds());

        watchHistoryRepository.save(history);
    }
}