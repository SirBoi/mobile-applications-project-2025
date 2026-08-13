package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.ActiveDriverDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DriverAPI {
    @GET("api/drivers/active")
    Call<List<ActiveDriverDTO>> getActiveDrivers();
}