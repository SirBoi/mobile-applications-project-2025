package com.example.mobile_applications_project_2025;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.NotificationPoller;
import com.example.mobile_applications_project_2025.Network.UserActivityTracker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Osmdroid konfiguracija (user agent) se sada postavlja SAMO jednom,
        // u MyApp.onCreate(), pre nego što bilo koja Activity/Fragment krene.
        // Ponovni load()/setUserAgentValue() ovde bi mogao da prepiše tu vrednost.

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        requestNotificationPermissionIfNeeded();

        bottomNav = findViewById(R.id.bottomNav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        boolean loggedIn = SessionManager.isLoggedIn();

        NavGraph graph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        graph.setStartDestination(loggedIn ? R.id.homeFragment : R.id.unregisteredHomeFragment);
        navController.setGraph(graph);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (!SessionManager.isLoggedIn()) {
                return false;
            }

            Role role = SessionManager.getRole();
            if (role == null) role = Role.Passenger;

            if (id == R.id.myAccountFragment) {
                navController.navigate(R.id.myAccountFragment);
                return true;

            } else if (id == R.id.driverRideHistoryFragment) {
                if (role == Role.Admin) {
                    navController.navigate(R.id.userSearchFragment);
                    return true;
                } else if (role == Role.Driver) {
                    navController.navigate(R.id.driverRideHistoryFragment);
                    return true;
                } else {
                    navController.navigate(R.id.passengerRideHistoryFragment);
                    return true;
                }

            } else if (id == R.id.homeFragment) {
                navController.navigate(R.id.homeFragment);
                return true;

            } else if (id == R.id.chatFragment) {
                if (role == Role.Admin) {
                    navController.navigate(R.id.adminUserListFragment);
                    return true;
                } else {
                    navController.navigate(R.id.chatFragment);
                    return true;
                }

            } else if (id == R.id.statsFragment) {
                if (role == Role.Admin) {
                    navController.navigate(R.id.adminRideOverviewFragment);
                } else {
                    navController.navigate(R.id.statsFragment);
                }
                return true;
            }

            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        // Objedinjeni Reselected listener (prethodno si imao dva koja su se preklapala)
        bottomNav.setOnItemReselectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.adminUserListFragment) {
                navController.popBackStack(R.id.adminUserListFragment, false);
                return;
            }

            if (id == R.id.myAccountFragment) {
                navController.popBackStack(R.id.passengerRideOverviewFragment, false);
                return;
            }

            navController.popBackStack(id, false);
        });

        bottomNav.setSelectedItemId(R.id.homeFragment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            setBottomNavEnabled(SessionManager.isLoggedIn());
        });

        setBottomNavEnabled(loggedIn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBottomNavEnabled(SessionManager.isLoggedIn());
    }

    private void setBottomNavEnabled(boolean enabled) {
        bottomNav.setAlpha(enabled ? 1.0f : 0.35f);
        bottomNav.setEnabled(enabled);
        bottomNav.setClickable(enabled);
        bottomNav.setFocusable(enabled);

        Menu menu = bottomNav.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setEnabled(enabled);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        RegisteredUser u = SessionManager.getUser();
        if (u == null || u.getId() == null) return;

        UserActivityTracker.getInstance().start();
        NotificationPoller.getInstance(this).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserActivityTracker.getInstance().stop();
        NotificationPoller.getInstance(this).stop();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }
}