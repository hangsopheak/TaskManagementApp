package com.example.taskmanagement.background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SimpleWork extends Worker {
    public SimpleWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SimpleWork", "WorkManager is running background task...");
        try {
            Thread.sleep(3000); // Simulate work
        } catch (InterruptedException e) {
            return Result.failure();
        }
        Log.d("SimpleWork", "Background task completed.");
        return Result.success();
    }
}