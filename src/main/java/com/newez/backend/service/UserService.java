package com.newez.backend.service;

import com.newez.backend.domain.AppVersion; // [추가]
import com.newez.backend.domain.User;
import com.newez.backend.dto.ComprehensiveUserResponse;
import com.newez.backend.repository.AppVersionRepository; // [추가]
import com.newez.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Paths; // [추가]
import java.util.List;
import java.util.Optional; // [추가]

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GeoIpService geoIpService;
    // [추가] 기존에 만들어진 AppVersionRepository를 주입받습니다.
    private final AppVersionRepository appVersionRepository;

    // --- 기존 updateUserIpByDeviceId 메소드는 그대로 둡니다 ---
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

        // --- [수정] 앱 업데이트 정보 로직을 실제 DB 조회로 변경합니다. ---

        // 1. DB에서 가장 최신 앱 버전 정보를 가져옵니다.
        Optional<AppVersion> latestVersionOpt = appVersionRepository.findLatestVersion();
        if (latestVersionOpt.isPresent()) {
            AppVersion latestVersion = latestVersionOpt.get();
            ComprehensiveUserResponse.AppUpdateInfo updateInfo = new ComprehensiveUserResponse.AppUpdateInfo();

            updateInfo.setLatestVersion(latestVersion.getVersionName());

            // 2. [중요] 실제 다운로드 가능한 URL을 만들어줍니다.
            // TODO: '/api/updates/download'와 같은 실제 파일 다운로드 API 주소를 만들어야 합니다.
            String fileName = Paths.get(latestVersion.getFilePath()).getFileName().toString();
            String downloadUrl = "http://192.168.0.2:8081/downloads/" + fileName; // 임시 URL 예시
            updateInfo.setDownloadUrl(downloadUrl);

            // 3. [중요] AppVersion 엔티티에 isForced 컬럼이 없으므로, 우선 false로 고정합니다.
            // TODO: DB에 is_forced 컬럼을 추가하고, 그 값을 사용하도록 수정해야 합니다.
            updateInfo.setForced(false);

            response.setAppUpdateInfo(updateInfo);
        }

        // --- 나머지 정보들은 그대로 둡니다 ---

        // TODO: 실제 공지사항 로직으로 교체해야 합니다.
        response.setNotice("서버 안정화 작업 안내: 8월 14일(목) 오전 2시~4시");

        // TODO: 실제 광고 목록 로직으로 교체해야 합니다.
        response.setAdList(List.of("http://example.com/ad1.png", "http://example.com/ad2.png"));

        return response;
    }
}
