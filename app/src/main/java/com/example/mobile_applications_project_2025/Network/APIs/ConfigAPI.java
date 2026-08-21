package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.ConfigDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface ConfigAPI {
    // 2.14 - cena po tipu vozila (standardno/luksuzno/kombi)
    @GET("api/config")
    Call<ConfigDTO> get();

    @PUT("api/config")
    Call<ConfigDTO> update(@Body ConfigDTO body);
}