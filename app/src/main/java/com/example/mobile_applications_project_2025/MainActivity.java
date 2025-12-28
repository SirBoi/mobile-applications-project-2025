package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnPrijava = findViewById(R.id.btnPrijava);
        btnPrijava.setOnClickListener(v -> {
            //Intent intent = new Intent(MainActivity.this, RideTrackingActivity.class);
            Intent intent = new Intent(MainActivity.this, DriverHistorySimpleActivity.class);

            startActivity(intent);
        });


        View markerBusy1 = findViewById(R.id.markerBusy1);
        View markerFree1 = findViewById(R.id.markerFree1);
        View markerFree2 = findViewById(R.id.markerFree2);

        markerBusy1.setOnClickListener(v ->
                Toast.makeText(this, "Vozilo: Zauzeto", Toast.LENGTH_SHORT).show()
        );
        markerFree1.setOnClickListener(v ->
                Toast.makeText(this, "Vozilo: Slobodno", Toast.LENGTH_SHORT).show()
        );
        markerFree2.setOnClickListener(v ->
                Toast.makeText(this, "Vozilo: Slobodno", Toast.LENGTH_SHORT).show()
        );
    }
}
