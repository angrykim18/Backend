package com.newez.backend.dto;


public class HqActivationRequest {
    private String deviceId;
    private String adminPassword;

    // Getter
    public String getDeviceId() {
        return deviceId;
    }

    public String getAdminPassword() {
        return adminPassword;
    }
}