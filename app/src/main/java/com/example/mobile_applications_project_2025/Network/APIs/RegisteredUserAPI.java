package com.example.mobile_applications_project_2025.Network.APIs;

import com.example.mobile_applications_project_2025.DTO.PageResponseDTO;
import com.example.mobile_applications_project_2025.DTO.UpdateDriverDTO;
import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.DriverAccountUpdateRequest;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Model.Route;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RegisteredUserAPI {
    @GET("api/users/login")
    Call<JsonObject> login(@Query("mail") String mail, @Query("password") String password);

    @GET("api/users/{id}/picture")
    Call<ResponseBody> getProfilePicture(@Path("id") long id);

    @Multipart
    @PUT("api/users/{id}/picture")
    Call<Void> uploadProfilePicture(
            @Path("id") long id,
            @Part MultipartBody.Part file
    );

    @PUT("api/users/{id}")
    Call<JsonObject> updateUser(
            @Path("id") long id,
            @Body Object user
    );

    @POST("api/drivers/{driverId}/account-update-request")
    Call<Void> createDriverAccountUpdateRequest(
            @Path("driverId") long driverId,
            @Body UpdateDriverDTO dto
    );

    @GET("api/admin/driver-account-update-requests")
    Call<List<DriverAccountUpdateRequest>> getAllDriverAccountUpdateRequests();

    @POST("api/admin/driver-account-update-requests/{driverId}/approve")
    Call<Void> approveDriverAccountUpdateRequest(@Path("driverId") long driverId);

    @POST("api/admin/driver-account-update-requests/{driverId}/reject")
    Call<Void> rejectDriverAccountUpdateRequest(@Path("driverId") long driverId);

    @POST("api/users")
    Call<JsonObject> createUser(@Body JsonObject user);

    @POST("api/users/activate")
    Call<ResponseBody> activateAccount(@Query("token") String token, @Query("password") String password);

    @GET("api/users/{id}/favourite-routes")
    Call<PageResponseDTO<Route>> getFavouriteRoutesPaged(
            @Path("id") long userId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/users/drivers")
    Call<PageResponseDTO<Driver>> getDriversPaged(
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("api/users/{id}/block")
    Call<RegisteredUser> blockUser(@Path("id") long id, @Body Map<String, String> body);

    @POST("api/users/{id}/unblock")
    Call<RegisteredUser> unblockUser(@Path("id") long id);

    @GET("api/users/paged")
    Call<PageResponseDTO<RegisteredUser>> getAllUsersPaged(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/users/{id}")
    Call<RegisteredUser> getUserById(@Path("id") long id);
}