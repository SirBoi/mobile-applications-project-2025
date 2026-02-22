package com.example.mobile_applications_project_2025.Network;

import android.os.Handler;
import android.os.Looper;

import com.example.mobile_applications_project_2025.Network.APIs.UserActivityAPI;
import com.example.mobile_applications_project_2025.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivityTracker {

    private static final long HEARTBEAT_MS = 20_000;

    private static UserActivityTracker instance;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final UserActivityAPI api;

    private boolean running = false;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (!running) return;

            if (SessionManager.isLoggedIn() && SessionManager.getUser() != null && SessionManager.getUser().getId() != null) {
                Long userId = SessionManager.getUser().getId();
                api.heartbeat(userId).enqueue(new Callback<Void>() {
                    @Override public void onResponse(Call<Void> call, Response<Void> response) { }
                    @Override public void onFailure(Call<Void> call, Throwable t) { }
                });
            }

            handler.postDelayed(this, HEARTBEAT_MS);
        }
    };

    private UserActivityTracker() {
        api = ApiClient.getRetrofit().create(UserActivityAPI.class);
    }

    public static synchronized UserActivityTracker getInstance() {
        if (instance == null) instance = new UserActivityTracker();
        return instance;
    }

    public void start() {
        if (running) return;
        running = true;
        handler.post(task);
    }

    public void stop() {
        if (!running) return;
        running = false;
        handler.removeCallbacks(task);

        if (SessionManager.isLoggedIn() && SessionManager.getUser() != null && SessionManager.getUser().getId() != null) {
            Long userId = SessionManager.getUser().getId();
            api.stop(userId).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> call, Response<Void> response) { }
                @Override public void onFailure(Call<Void> call, Throwable t) { }
            });
        }
    }
}