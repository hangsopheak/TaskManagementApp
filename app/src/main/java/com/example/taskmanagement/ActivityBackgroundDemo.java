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
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.taskmanagement.background.FileUploadService;
import com.example.taskmanagement.background.LogUploadWorker;
import com.example.taskmanagement.background.MusicPlaybackService;
import com.example.taskmanagement.background.QuoteWorker;
import com.example.taskmanagement.databinding.ActivityBackgroundDemoBinding;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        binding.btnMainThread.setOnClickListener(v -> {
            log("[MainThread] Start");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
            }
            log("[MainThread] Done!");
        });

        binding.btnThread.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    Thread.sleep(10000); // Simulate long operation
                    log("Thread without handler] Done!");
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread interrupted", e);
                }
            });
        });

        // Simulate background task with Thread + Handler
        binding.btnThreadHandler.setOnClickListener(v -> {
            log("[Thread+Handler] Start");

            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    Thread.sleep(3000); // Simulate long operation
                    uiHandler.post(() -> {
                        log("[Thread+Handler] Done!");
                        Toast.makeText(this, "Thread+Handler Task Done", Toast.LENGTH_SHORT).show();
                    });
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread interrupted", e);
                }
            });
        });

        // Start a StartedService (simple one-time task)
        binding.btnStartedService.setOnClickListener(v -> {
            log("[StartedService] Starting file upload service...");
            Intent intent = new Intent(this, FileUploadService.class);
            startService(intent);
        });

        // Start a ForegroundService
        binding.btnForegroundService.setOnClickListener(v -> {
            log("[ForegroundService] Starting music playback service...");
            Intent intent = new Intent(this, MusicPlaybackService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        });



        binding.btnPeriodicWork.setOnClickListener(v -> {
            PeriodicWorkRequest quoteWork = new PeriodicWorkRequest.Builder(QuoteWorker.class, 15, TimeUnit.MINUTES)
                    .addTag("periodic_quote")
                    .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "QuotePeriodicTask",
                    ExistingPeriodicWorkPolicy.KEEP,
                    quoteWork
            );

            Toast.makeText(this, "🔁 Periodic quote generation scheduled", Toast.LENGTH_SHORT).show();
        });

        binding.btnConstrainedWork.setOnClickListener(v -> {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED) // Wi-Fi
                    .build();

            OneTimeWorkRequest uploadLogs = new OneTimeWorkRequest.Builder(LogUploadWorker.class)
                    .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(this).enqueue(uploadLogs);

            Toast.makeText(this, "📤 Log upload scheduled (Wi-Fi + Charging)", Toast.LENGTH_SHORT).show();
        });
    }

    private void log(String message) {
        Log.d(TAG, message);
        binding.logView.append(message + "\n");
    }
}

