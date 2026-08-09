package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.PageResponseDTO;
import com.example.mobile_applications_project_2025.DTO.RideCreateWithCriteriaRequestDTO;
import com.example.mobile_applications_project_2025.Model.Ride;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideAPI {
    @GET("api/config")
    Call<ResponseBody> getConfig();

    @POST("api/rides/create")
    Call<ResponseBody> createWithDriverMatch(@Body RideCreateWithCriteriaRequestDTO body);

    @GET("api/rides/driver/{driverId}")
    Call<PageResponseDTO<Ride>> getDriverRidesPaged(
            @Path("driverId") Long driverId,
            @Query("statuses") List<String> statuses,
            @Query("from") String fromIso,
            @Query("to") String toIso,
            @Query("page") int page,
            @Query("size") int size
    );

    @PATCH("api/rides/{id}/start")
    Call<Ride> startRide(@Path("id") Long rideId);

    @PATCH("api/rides/{id}/finish")
    Call<Ride> finishRide(@Path("id") Long rideId);

    @PATCH("api/rides/{id}/cancel")
    Call<Ride> cancelRide(@Path("id") Long rideId);

    @GET("api/rides/passenger/{passengerId}")
    Call<PageResponseDTO<Ride>> getPassengerRidesPaged(
            @Path("passengerId") Long passengerId,
            @Query("statuses") List<String> statuses,
            @Query("from") String fromIso,
            @Query("to") String toIso,
            @Query("favoritesOnly") boolean favoritesOnly,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("api/rides/{rideId}/favorite/{passengerId}")
    Call<Void> favoriteRide(@Path("rideId") Long rideId, @Path("passengerId") Long passengerId);

    @DELETE("api/rides/{rideId}/favorite/{passengerId}")
    Call<Void> unfavoriteRide(@Path("rideId") Long rideId, @Path("passengerId") Long passengerId);

    @GET("api/rides/passenger/{passengerId}/current")
    Call<Ride> getPassengerCurrentRide(@Path("passengerId") Long passengerId);

    @GET("api/rides/driver/{driverId}/current")
    Call<Ride> getDriverCurrentRide(@Path("driverId") Long driverId);

    @GET("api/rides/driver/{driverId}/next")
    Call<Ride> getDriverNextScheduledRide(@Path("driverId") Long driverId);
}