package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.NotificationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationAPI {
    @GET("api/notifications/user/{userId}")
    Call<List<NotificationDTO>> getForUser(@Path("userId") Long userId);

    @PUT("api/notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") Long id);
}