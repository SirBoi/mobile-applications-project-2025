package com.example.mobile_applications_project_2025.Network;

public final class BaseUrl {
    private BaseUrl() {}

    public static String get() {
        // emulator: 10.0.2.2, real device: your PC IP
        return "http://10.0.2.2:8080/";
    }
}
