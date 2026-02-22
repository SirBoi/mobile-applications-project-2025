package com.example.mobile_applications_project_2025;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.mobile_applications_project_2025.Network.UserActivityTracker;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
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