package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class RideTrackingActivity extends AppCompatActivity {

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride_tracking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvEta = findViewById(R.id.tvEta);
        TextInputEditText etNote = findViewById(R.id.etNote);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnReport = findViewById(R.id.btnReport);

        // UI-only: mock countdown (npr. 4:35 -> 275 sekundi)
        startMockEtaCountdown(tvEta, 4 * 60 + 35);

        btnSend.setOnClickListener(v -> {
            String text = etNote.getText() != null ? etNote.getText().toString().trim() : "";
            if (text.isEmpty()) {
                Toast.makeText(this, "Unesi napomenu pre slanja.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Poslata prijava " + text, Toast.LENGTH_SHORT).show();
            etNote.setText("");
        });

//        btnReport.setOnClickListener(v ->
//                Toast.makeText(this, "UI-only: report forma je ispod mape.", Toast.LENGTH_SHORT).show()
//        );
    }

    private void startMockEtaCountdown(TextView tvEta, int totalSeconds) {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(totalSeconds * 1000L, 1000L) {
            int secondsLeft = totalSeconds;

            @Override
            public void onTick(long millisUntilFinished) {
                secondsLeft--;
                int m = Math.max(0, secondsLeft) / 60;
                int s = Math.max(0, secondsLeft) % 60;
                tvEta.setText(String.format("%02d:%02d", m, s));
            }

            @Override
            public void onFinish() {
                tvEta.setText("00:00");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
