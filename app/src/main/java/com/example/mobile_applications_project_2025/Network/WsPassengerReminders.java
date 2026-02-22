package com.example.mobile_applications_project_2025.Network;

import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

public class WsPassengerReminders {

    public interface Listener {
        void onRideReminder(long rideId, int minutesBefore, String message);
    }

    private static final String TAG = "WsPassengerReminders";
    private final Gson gson = new Gson();

    private StompClient stompClient;
    private Disposable topicSub;
    private Disposable lifecycleSub;

    public void connect(String wsUrl, long passengerId, Listener listener) {
        disconnect();

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);

        lifecycleSub = stompClient.lifecycle().subscribe(ev -> {
            if (ev.getType() == LifecycleEvent.Type.ERROR) {
                Log.e(TAG, "WS error", ev.getException());
            }
        });

        stompClient.connect();

        String topic = "/topic/passenger/" + passengerId;
        topicSub = stompClient.topic(topic).subscribe(msg -> {
            try {
                PassengerRideReminderNotification n =
                        gson.fromJson(msg.getPayload(), PassengerRideReminderNotification.class);

                if (listener != null && n != null) {
                    long rideId = n.rideId != null ? n.rideId : -1L;
                    int mb = n.minutesBefore != null ? n.minutesBefore : -1;
                    String text = n.message != null ? n.message : "Ride reminder";
                    listener.onRideReminder(rideId, mb, text);
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

    public static class PassengerRideReminderNotification {
        public Long rideId;
        public Integer minutesBefore;
        public String message;
    }
}