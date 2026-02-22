package com.example.mobile_applications_project_2025.Network.APIs;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserActivityAPI {

    @POST("api/activity/heartbeat/{userId}")
    Call<Void> heartbeat(@Path("userId") Long userId);

    @POST("api/activity/stop/{userId}")
    Call<Void> stop(@Path("userId") Long userId);
}