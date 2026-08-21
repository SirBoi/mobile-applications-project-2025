package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.DriverReportCreateRequestDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DriverReportAPI {
    @POST("api/driverreports/create")
    Call<ResponseBody> createReport(@Body DriverReportCreateRequestDTO body);
}