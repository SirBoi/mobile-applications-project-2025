package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.UserActivityTracker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        boolean loggedIn = SessionManager.isLoggedIn();
        //String role = SessionManager.getRole(this);
        //if (role == null) role = "passenger";

        NavGraph graph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        graph.setStartDestination(loggedIn ? R.id.homeFragment : R.id.unregisteredHomeFragment);
        navController.setGraph(graph);

        //NavigationUI.setupWithNavController(bottomNav, navController); cemu sluzi???

        bottomNav.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.adminUserListFragment) {
                // Vrati na listu "Recent Chats" kad ponovo klikneš na Chat dugme u navbaru
                navController.popBackStack(R.id.adminUserListFragment, false);
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (!SessionManager.isLoggedIn()) {
                return false;
            }

            Role role = SessionManager.getRole();
            if (role == null) role = Role.Passenger;

            // Naredaj rutiranje za svaki id
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
                if (role.equals("admin")) {
                    navController.navigate(R.id.adminUserListFragment);
                    return true;
                } else if (role.equals("driver")) {
                    navController.navigate(R.id.chatFragment);
                    return true;
                } else {
                    navController.navigate(R.id.chatFragment);
                    return true;
                }

            } else if (id == R.id.statsFragment) {
                if (role.equals("admin")) {
                    navController.navigate(R.id.statsFragment);
                    return true;
                } else if (role.equals("driver")) {
                    navController.navigate(R.id.statsFragment);
                    return true;
                } else {
                    navController.navigate(R.id.statsFragment);
                    return true;
                }
            }

            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        bottomNav.setOnItemReselectedListener(item -> {
            int id = item.getItemId();

            // Naredaj rutiranje za svaki id
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

        //NavigationUI.setupWithNavController(bottomNav, navController); sto su dva???

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
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserActivityTracker.getInstance().stop();
    }
}