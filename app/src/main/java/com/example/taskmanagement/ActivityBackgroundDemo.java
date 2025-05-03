package com.example.taskmanagement;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.taskmanagement.background.SimpleForegroundService;
import com.example.taskmanagement.background.SimpleStartedService;
import com.example.taskmanagement.background.SimpleWork;
import com.example.taskmanagement.databinding.ActivityBackgroundDemoBinding;

public class ActivityBackgroundDemo extends AppCompatActivity {

    private static final String TAG = "BackgroundDemo";
    private ActivityBackgroundDemoBinding binding;
    private Handler uiHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBackgroundDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uiHandler = new Handler(Looper.getMainLooper());

        // Simulate background task with Thread + Handler
        binding.btnThreadHandler.setOnClickListener(v -> {
            log("[Thread+Handler] Start");
            new Thread(() -> {
                try {
                    Thread.sleep(3000); // Simulate long operation
                    uiHandler.post(() -> {
                        log("[Thread+Handler] Done!");
                        Toast.makeText(this, "Thread+Handler Task Done", Toast.LENGTH_SHORT).show();
                    });
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread interrupted", e);
                }
            }).start();
        });

        // Start a StartedService (simple one-time task)
        binding.btnStartedService.setOnClickListener(v -> {
            log("[StartedService] Starting service...");
            Intent intent = new Intent(this, SimpleStartedService.class);
            startService(intent);
        });

        // Start a ForegroundService
        binding.btnForegroundService.setOnClickListener(v -> {
            log("[ForegroundService] Starting...");
            Intent intent = new Intent(this, SimpleForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        });

        // Enqueue a WorkManager task
        binding.btnWorkManager.setOnClickListener(v -> {
            log("[WorkManager] Enqueuing background work...");
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SimpleWork.class)
                    .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(this).enqueue(workRequest);
        });
    }

    private void log(String message) {
        Log.d(TAG, message);
        binding.logView.append(message + "\n");
    }
}

