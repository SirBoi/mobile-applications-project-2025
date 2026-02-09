package com.example.mobile_applications_project_2025.Network;

public final class API {

    private API() {}

    public static <T> T of(Class<T> apiInterface) {
        return ApiClient.getRetrofit().create(apiInterface);
    }
}
