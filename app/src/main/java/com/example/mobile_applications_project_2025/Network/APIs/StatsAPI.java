package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.StatsResponseDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StatsAPI {

    @GET("api/stats/passenger/{passengerId}")
    Call<StatsResponseDTO> passengerStats(
            @Path("passengerId") long passengerId,
            @Query("from") String fromIsoDate,
            @Query("to") String toIsoDate
    );

    @GET("api/stats/driver/{driverId}")
    Call<StatsResponseDTO> driverStats(
            @Path("driverId") long driverId,
            @Query("from") String fromIsoDate,
            @Query("to") String toIsoDate
    );

    @GET("api/stats/admin")
    Call<StatsResponseDTO> adminStats(
            @Query("from") String fromIsoDate,
            @Query("to") String toIsoDate
    );
}