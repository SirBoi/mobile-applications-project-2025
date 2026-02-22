package com.example.mobile_applications_project_2025;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;

public class SessionManager {
    private static RegisteredUser user;
    private static Role role;

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static RegisteredUser getUser() {
        if (user != null) return user;
        return null;
    }

    public static void setUser(RegisteredUser u) {
        user = u;
    }

    public static Role getRole() {
        if (role != null) return role;
        return null;
    }

    public static void setRole(Role r) {
        role = r;
    }

    public static void clear() {
        user = null;
        role = null;
    }
}
