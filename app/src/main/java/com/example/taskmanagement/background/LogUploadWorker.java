package com.example.taskmanagement.background;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class LogUploadWorker extends Worker {

    public LogUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Simulate upload
            Thread.sleep(3000);
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "✅ Logs uploaded", Toast.LENGTH_SHORT).show());

            return Result.success();
        } catch (InterruptedException e) {
            return Result.failure();
        }
    }
}