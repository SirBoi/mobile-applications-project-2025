package com.example.mobile_applications_project_2025.Network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mobile_applications_project_2025.DTO.NotificationDTO;
import com.example.mobile_applications_project_2025.Network.APIs.NotificationAPI;
import com.example.mobile_applications_project_2025.R;
import com.example.mobile_applications_project_2025.SessionManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Periodično povlači notifikacije ulogovanog korisnika sa servera (2.4.2 i
 * ostale notifikacije koje se čuvaju u bazi) i za svaku novu prikazuje pravu
 * Android sistemsku notifikaciju (nefunkcionalni zahtev: "Koristiti ugrađen
 * sistem za notifikacije u Android-u").
 *
 * Prati isti obrazac kao UserActivityTracker: singleton, start()/stop() se
 * pozivaju uz login/logout.
 */
public class NotificationPoller {

    private static final String CHANNEL_ID = "ride_notifications";
    private static final long POLL_INTERVAL_MS = 8_000;
    private static final String PREFS_NAME = "notification_poller_prefs";
    private static final String PREF_SHOWN_IDS = "shown_notification_ids";

    private static NotificationPoller instance;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final NotificationAPI api;
    private final Context appContext;
    private final Set<Long> shownIds;

    private boolean running = false;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            poll();
            handler.postDelayed(this, POLL_INTERVAL_MS);
        }
    };

    private NotificationPoller(Context context) {
        this.appContext = context.getApplicationContext();
        this.api = ApiClient.getRetrofit().create(NotificationAPI.class);
        createChannel();
        this.shownIds = loadShownIds();
    }

    public static synchronized NotificationPoller getInstance(Context context) {
        if (instance == null) instance = new NotificationPoller(context);
        return instance;
    }

    public void start() {
        if (running) return;
        running = true;
        handler.post(task);
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(task);
    }

    private void poll() {
        if (!SessionManager.isLoggedIn() || SessionManager.getUser() == null
                || SessionManager.getUser().getId() == null) {
            return;
        }
        Long userId = SessionManager.getUser().getId();

        api.getForUser(userId).enqueue(new Callback<List<NotificationDTO>>() {
            @Override
            public void onResponse(Call<List<NotificationDTO>> call, Response<List<NotificationDTO>> response) {
                if (!running || !response.isSuccessful() || response.body() == null) return;
                handleNotifications(response.body());
            }

            @Override
            public void onFailure(Call<List<NotificationDTO>> call, Throwable t) {
                // Tiho ignorišemo - pokušaćemo ponovo na sledećem pollu.
            }
        });
    }

    private void handleNotifications(List<NotificationDTO> notifications) {
        boolean changed = false;
        for (NotificationDTO n : notifications) {
            if (n.getId() == null || shownIds.contains(n.getId())) continue;

            showSystemNotification(n);
            shownIds.add(n.getId());
            changed = true;
        }
        if (changed) persistShownIds();
    }

    private void showSystemNotification(NotificationDTO n) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle("Obaveštenje o vožnji")
                .setContentText(n.getMessage())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(n.getMessage()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat.from(appContext)
                    .notify(n.getId().intValue(), builder.build());
        } catch (SecurityException e) {
            // Korisnik nije dao POST_NOTIFICATIONS dozvolu (Android 13+) - tiho preskačemo.
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Obaveštenja o vožnji",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Obaveštenja o statusu vožnje, dodavanju na vožnju, itd.");
            NotificationManager manager = appContext.getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Set<Long> loadShownIds() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet(PREF_SHOWN_IDS, new HashSet<>());
        Set<Long> ids = new HashSet<>();
        if (stored != null) {
            for (String s : stored) {
                try {
                    ids.add(Long.parseLong(s));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return ids;
    }

    private void persistShownIds() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> asStrings = new HashSet<>();
        for (Long id : shownIds) asStrings.add(String.valueOf(id));
        prefs.edit().putStringSet(PREF_SHOWN_IDS, asStrings).apply();
    }
}