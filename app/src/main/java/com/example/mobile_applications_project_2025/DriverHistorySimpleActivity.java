package com.example.mobile_applications_project_2025;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class DriverHistorySimpleActivity extends AppCompatActivity {

    private TextInputEditText etFromDate, etToDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_history_simple);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historyRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        Button btnApply = findViewById(R.id.btnApply);

        etFromDate.setOnClickListener(v -> showDatePicker(etFromDate));
        etToDate.setOnClickListener(v -> showDatePicker(etToDate));

        btnApply.setOnClickListener(v -> {
            String from = etFromDate.getText() != null ? etFromDate.getText().toString() : "";
            String to = etToDate.getText() != null ? etToDate.getText().toString() : "";
            Toast.makeText(this, " " + from + " → " + to, Toast.LENGTH_SHORT).show();
        });
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dd = String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth, (month + 1), year);
            target.setText(dd);
        }, y, m, d).show();
    }
}
