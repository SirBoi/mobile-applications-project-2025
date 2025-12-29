package com.example.mobile_applications_project_2025;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobile_applications_project_2025.Model.User;

public class SessionManager {
    private static final String PREF = "session";
    private static String KEY_LOGGED_IN = "logged_in"; // izbrisi nakon kt1
    private static User cachedUser;
    private static String cachedRole;

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static boolean isLoggedIn(Context c) {
        return prefs(c).getBoolean(KEY_LOGGED_IN, false);
        //return cachedUser != null; odkomentarisi nakon kt1
    }

    public static void setLoggedIn(Context c, boolean value) { // izbrisi nakon kt1
        SharedPreferences.Editor ed = c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit();
        ed.putBoolean(KEY_LOGGED_IN, value);
        ed.apply();
    }

    public static User getUser(Context c) {
        if (cachedUser != null) return cachedUser;
        return null;
    }

    public static void setUser(Context c, User u) {
        SharedPreferences.Editor e = prefs(c).edit();
        cachedUser = u;
    }

    public static String getRole(Context c) {
        if (cachedRole != null) return cachedRole;
        return null;
    }

    public static void setRole(Context c, String role) {
        SharedPreferences.Editor e = prefs(c).edit();
        cachedRole = role;
    }

    public static void clear(Context c) {
        prefs(c).edit().clear().apply();
        SharedPreferences.Editor ed = c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit();
        ed.putBoolean(KEY_LOGGED_IN, false);
        cachedUser = null;
        cachedRole = null;
    }
}
