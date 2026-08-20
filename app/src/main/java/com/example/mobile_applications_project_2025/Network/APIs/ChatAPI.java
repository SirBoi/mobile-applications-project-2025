package com.example.mobile_applications_project_2025.Network.APIs;

import java.util.List;

import com.example.mobile_applications_project_2025.DTO.ChatResponseDTO;
import com.example.mobile_applications_project_2025.DTO.MessageResponseDTO;
import com.example.mobile_applications_project_2025.DTO.MessageSendRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatAPI {
    // 2.11 - live podrska: jedan chat po korisniku, svi admini vide isti chat.
    @GET("api/chats/user/{userId}")
    Call<ChatResponseDTO> getOrCreateChat(@Path("userId") Long userId);

    @GET("api/chats/user/{userId}/messages")
    Call<List<MessageResponseDTO>> getMessages(@Path("userId") Long userId);

    // admin inbox - lista svih chat-ova
    @GET("api/chats")
    Call<List<ChatResponseDTO>> getAllChats();

    @POST("api/messages/send")
    Call<MessageResponseDTO> sendMessage(@Body MessageSendRequestDTO body);
}