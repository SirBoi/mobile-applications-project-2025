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
        NavigationUI.setupWithNavController(bottomNav, navController);

        /*
        bottomNav.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.adminUserListFragment) {
                // Vrati na listu "Recent Chats" kad ponovo klikneš na Chat dugme u navbaru
                navController.popBackStack(R.id.adminUserListFragment, false);
            }
        });
         */

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean loggedIn = SessionManager.isLoggedIn(this);

        NavGraph graph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        graph.setStartDestination(loggedIn ? R.id.homeFragment : R.id.unregisteredHomeFragment);
        navController.setGraph(graph);

        NavigationUI.setupWithNavController(bottomNav, navController);

        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            setBottomNavEnabled(SessionManager.isLoggedIn(this));
        });

        setBottomNavEnabled(loggedIn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBottomNavEnabled(SessionManager.isLoggedIn(this));
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
}