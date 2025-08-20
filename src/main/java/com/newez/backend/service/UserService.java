package com.newez.backend.service;

import com.newez.backend.domain.AppVersion;
import com.newez.backend.domain.User;
import com.newez.backend.dto.ComprehensiveUserResponse;
import com.newez.backend.repository.AppVersionRepository;
import com.newez.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import com.newez.backend.domain.Notice;
import com.newez.backend.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GeoIpService geoIpService;
    private final AppVersionRepository appVersionRepository;
    private final NoticeRepository noticeRepository;

    @Value("${file.download-base-url}")
    private String downloadBaseUrl;


    @Transactional
    public void updateUserIpByDeviceId(String deviceId, String ip) {
        User user = userRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id=" + deviceId));
        String country = geoIpService.getCountry(ip);
        user.setIp(ip);
        user.setCountry(country);
    }


    @Transactional(readOnly = true)
    public ComprehensiveUserResponse getComprehensiveUserInfo(String deviceId) {
        User user = userRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. deviceId=" + deviceId));

        ComprehensiveUserResponse response = new ComprehensiveUserResponse();

        response.setUserName(user.getName());
        response.setSubscriptionEndDate(user.getSubscriptionEndDate());
        response.setAdultContentAllowed(user.isAdultContentAllowed());
        response.setUserGroup(user.getUserGroup());


        Optional<AppVersion> latestVersionOpt = appVersionRepository.findLatestVersion();
        if (latestVersionOpt.isPresent()) {
            AppVersion latestVersion = latestVersionOpt.get();
            ComprehensiveUserResponse.AppUpdateInfo updateInfo = new ComprehensiveUserResponse.AppUpdateInfo();

            updateInfo.setLatestVersion(latestVersion.getVersionName());


            String fileName = Paths.get(latestVersion.getFilePath()).getFileName().toString();
            String downloadUrl = downloadBaseUrl + fileName;
            updateInfo.setDownloadUrl(downloadUrl);


            updateInfo.setForced(false);

            response.setAppUpdateInfo(updateInfo);
        }


        String userGroup = user.getUserGroup();
        if (userGroup != null && !userGroup.isEmpty()) {
            // 1. 긴급 공지 조회
            noticeRepository.findTopByNoticeTypeAndIsActiveAndTargetGroupOrderByCreatedAtDesc("긴급", true, userGroup)
                    .ifPresent(notice -> response.setUrgentNotice(notice.getContent()));
            // 2. 일반 공지 조회
            noticeRepository.findTopByNoticeTypeAndIsActiveAndTargetGroupOrderByCreatedAtDesc("일반", true, userGroup)
                    .ifPresent(notice -> response.setGeneralNotice(notice.getContent()));
        }


        response.setAdList(List.of("http://example.com/ad1.png", "http://example.com/ad2.png"));

        return response;
    }
}
