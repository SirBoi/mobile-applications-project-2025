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

        // Slušamo događaje konekcije
        lifecycleSub = stompClient.lifecycle().subscribe(ev -> {
            switch (ev.getType()) {
                case OPENED:
                    Log.d(TAG, "WS konekcija otvorena. Pretplata na topik...");
                    // TEK KAD JE KONEKCIJA OTVORENA, PRETPLAĆUJEMO SE NA TOPIK
                    subscribeToTopic(passengerId, listener);
                    break;

                case ERROR:
                    Log.e(TAG, "WS error", ev.getException());
                    break;

                case CLOSED:
                    Log.d(TAG, "WS konekcija zatvorena");
                    break;
            }
        });

        stompClient.connect();
    }

    private void subscribeToTopic(long passengerId, Listener listener) {
        if (stompClient == null || !stompClient.isConnected()) return;

        // Očisti prethodnu pretplatu ako postoji
        if (topicSub != null && !topicSub.isDisposed()) {
            topicSub.dispose();
        }

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
        try { if (stompClient != null && stompClient.isConnected()) stompClient.disconnect(); } catch (Exception ignored) {}
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