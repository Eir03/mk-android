package com.example.notenote;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Button backButton = findViewById(R.id.btn_back_schedule);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}