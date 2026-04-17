package ru.goloskokov.backgroundnotification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnBackgroundTasks = findViewById(R.id.btn_background_tasks);
        Button btnNotifications = findViewById(R.id.btn_notifications);

        btnBackgroundTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BackgroundDemoActivity.class);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationDemoActivity.class);
            startActivity(intent);
        });
    }
}