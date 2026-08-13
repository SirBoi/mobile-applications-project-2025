package com.example.mobile_applications_project_2025;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.preference.PreferenceManager;

import com.example.mobile_applications_project_2025.Network.UserActivityTracker;

import org.osmdroid.config.Configuration;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Učitavanje osmdroid konfiguracije
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        // 2. Postavljanje STABILNOG User-Agent-a (isti pri svakom pokretanju, ne menja se)
        //    OSM tile serveri traže da User-Agent jasno i dosledno identifikuje aplikaciju.
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // 3. Inicijalizacija praćenja aktivnog stanja aplikacije (Foreground / Background)
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver());
    }

    static class AppLifecycleObserver implements androidx.lifecycle.LifecycleObserver {

        @androidx.lifecycle.OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_START)
        public void onEnterForeground() {
            UserActivityTracker.getInstance().start();
        }

        @androidx.lifecycle.OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_STOP)
        public void onEnterBackground() {
            UserActivityTracker.getInstance().stop();
        }
    }
}