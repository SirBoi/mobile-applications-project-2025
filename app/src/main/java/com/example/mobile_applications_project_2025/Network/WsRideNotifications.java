package com.example.mobile_applications_project_2025.Network;

import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class WsRideNotifications {

    public interface Listener {
        void onRideAssigned(long rideId, String message);
    }

    private static final String TAG = "WsRideNotifications";

    private final Gson gson = new Gson();

    private StompClient stompClient;
    private Disposable topicSub;
    private Disposable lifecycleSub;

    public void connect(String wsUrl, long driverId, Listener listener) {
        disconnect();

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);

        lifecycleSub = stompClient.lifecycle().subscribe(ev -> {
            if (ev.getType() == LifecycleEvent.Type.OPENED) {
                Log.i(TAG, "WS connected");
            } else if (ev.getType() == LifecycleEvent.Type.ERROR) {
                Log.e(TAG, "WS error", ev.getException());
            } else if (ev.getType() == LifecycleEvent.Type.CLOSED) {
                Log.i(TAG, "WS closed");
            }
        });

        stompClient.connect();

        String topic = "/topic/driver/" + driverId;
        topicSub = stompClient.topic(topic).subscribe(msg -> {
            try {
                DriverRideAssignedNotification n =
                        gson.fromJson(msg.getPayload(), DriverRideAssignedNotification.class);

                if (listener != null && n != null) {
                    long rideId = n.rideId != null ? n.rideId : -1L;
                    String text = n.message != null ? n.message : "New ride assigned";
                    listener.onRideAssigned(rideId, text);
                }
            } catch (Exception e) {
                Log.e(TAG, "Parse error: " + msg.getPayload(), e);
            }
        }, e -> Log.e(TAG, "Topic subscribe error", e));
    }

    public void disconnect() {
        try { if (topicSub != null && !topicSub.isDisposed()) topicSub.dispose(); } catch (Exception ignored) {}
        try { if (lifecycleSub != null && !lifecycleSub.isDisposed()) lifecycleSub.dispose(); } catch (Exception ignored) {}
        try { if (stompClient != null) stompClient.disconnect(); } catch (Exception ignored) {}

        topicSub = null;
        lifecycleSub = null;
        stompClient = null;
    }

    // payload model (matches backend DTO fields)
    public static class DriverRideAssignedNotification {
        public Long rideId;
        public String message;
    }
}