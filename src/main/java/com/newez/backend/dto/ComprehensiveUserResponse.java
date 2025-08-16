package com.newez.backend.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * 안드로이드에 전달할 모든 사용자 정보를 담는 '종합 선물 세트' DTO 입니다.
 * 이 파일은 새로 생성됩니다.
 */
public class ComprehensiveUserResponse {

    // 1. 사용자 이름
    private String userName;
    // 2. 시청 종료일자
    private LocalDate subscriptionEndDate;
    // 3. 성인방송 허용 유무
    private boolean adultContentAllowed;
    // 4. 공지사항
    private String notice;
    // 5. 셋탑 그룹
    private String userGroup;
    // 6. 앱 업데이트 정보
    private AppUpdateInfo appUpdateInfo;
    // 7. 광고 목록
    private List<String> adList;

    // --- 앱 업데이트 정보를 담을 내부 클래스 ---
    public static class AppUpdateInfo {
        private String latestVersion;
        private String downloadUrl;
        private boolean isForced;
        // Getters and Setters
        public String getLatestVersion() { return latestVersion; }
        public void setLatestVersion(String latestVersion) { this.latestVersion = latestVersion; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public boolean isForced() { return isForced; }
        public void setForced(boolean forced) { this.isForced = forced; }
    }

    // --- Getters and Setters ---
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public LocalDate getSubscriptionEndDate() { return subscriptionEndDate; }
    public void setSubscriptionEndDate(LocalDate subscriptionEndDate) { this.subscriptionEndDate = subscriptionEndDate; }
    public boolean isAdultContentAllowed() { return adultContentAllowed; }
    public void setAdultContentAllowed(boolean adultContentAllowed) { this.adultContentAllowed = adultContentAllowed; }
    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }
    public String getUserGroup() { return userGroup; }
    public void setUserGroup(String userGroup) { this.userGroup = userGroup; }
    public AppUpdateInfo getAppUpdateInfo() { return appUpdateInfo; }
    public void setAppUpdateInfo(AppUpdateInfo appUpdateInfo) { this.appUpdateInfo = appUpdateInfo; }
    public List<String> getAdList() { return adList; }
    public void setAdList(List<String> adList) { this.adList = adList; }
}
