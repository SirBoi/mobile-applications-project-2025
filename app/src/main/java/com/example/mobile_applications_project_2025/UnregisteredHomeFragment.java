package com.example.mobile_applications_project_2025;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class UnregisteredHomeFragment extends Fragment {

    private MapView mapView;
    private ActiveDriversMapController driversMapController;

    public UnregisteredHomeFragment() {
        // Required empty public constructor
    }

    public static UnregisteredHomeFragment newInstance(String param1, String param2) {
        UnregisteredHomeFragment fragment = new UnregisteredHomeFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OVDJE VIŠE NE STAVLJAMO Configuration.getInstance()...
        // Sve osmdroid postavke se izvršavaju centralizovano u MyApp.java
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unregistered_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicijalizacija OpenStreetMap prikaza
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.setTileSource(MapTileSourceProvider.MAPTILER_STREETS);
            mapView.setMultiTouchControls(true);

            // Pocetna lokacija (Novi Sad)
            GeoPoint startPoint = new GeoPoint(45.267136, 19.833549);
            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(startPoint);

            driversMapController = new ActiveDriversMapController(mapView);
        }

        Button btnPrijava = view.findViewById(R.id.uBtnPrijava);
        if (btnPrijava != null) {
            btnPrijava.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        if (driversMapController != null) {
            driversMapController.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        if (driversMapController != null) {
            driversMapController.stop();
        }
    }
}