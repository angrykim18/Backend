package com.newez.backend.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComprehensiveUserResponse {


    private String userName;
    private LocalDate subscriptionEndDate;
    private boolean adultContentAllowed;
    private String userGroup;
    private AppUpdateInfo appUpdateInfo;
    private List<String> adList;
    private String generalNotice;
    private String urgentNotice;


    @Getter
    @Setter
    public static class AppUpdateInfo {
        private String latestVersion;
        private String downloadUrl;
        private boolean isForced;
    }
}
