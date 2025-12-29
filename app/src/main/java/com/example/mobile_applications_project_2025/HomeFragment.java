package com.example.mobile_applications_project_2025;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class HomeFragment extends Fragment {
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnOdjava = view.findViewById(R.id.btnOdjava);
        btnOdjava.setOnClickListener(v -> {
            SessionManager.clear(requireContext());

            NavController navController = NavHostFragment.findNavController(this);

            NavGraph graph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            graph.setStartDestination(R.id.unregisteredHomeFragment);
            navController.setGraph(graph);
        });

        ExtendedFloatingActionButton fab = view.findViewById(R.id.fabOngoingRide);
        fab.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.passengerRideOverviewFragment));
    }
}