package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.DriverRatingCreateRequestDTO;
import com.example.mobile_applications_project_2025.DTO.DriverRatingResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DriverRatingAPI {
    // 2.8 - kreiranje ocene za zavrsenu voznju (odmah ili naknadno iz istorije).
    // Neuspesni odgovori (404/400/409/410) se citaju preko response.errorBody().
    @POST("api/driverratings/create")
    Call<DriverRatingResponseDTO> createRating(@Body DriverRatingCreateRequestDTO body);

    // Provera da li je voznja vec ocenjena (404 ako nije).
    @GET("api/driverratings/ride/{rideId}")
    Call<DriverRatingResponseDTO> getByRide(@Path("rideId") Long rideId);
}