package com.example.taskmanagement.background;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import androidx.work.impl.utils.futures.SettableFuture;

import com.example.taskmanagement.repository.IApiCallback;
import com.example.taskmanagement.repository.TaskRepository;
import com.google.common.util.concurrent.ListenableFuture;


public class SyncTaskWorker extends ListenableWorker {


    public SyncTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {

        // Must use SettableFuture to create a ListenableFuture you can manually set
        @SuppressLint("RestrictedApi")
        SettableFuture<Result> future = SettableFuture.create();
        try {
            // Create repository with application context
            TaskRepository repository = new TaskRepository(getApplicationContext());

            // Perform sync operation
            repository.syncTasks(new IApiCallback<String>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onSuccess(String result) {
                    // Send broadcast when sync completes successfully
                    Intent intent = new Intent("com.example.SYNC_COMPLETED");
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(intent);

                    // Complete the future with success
                    future.set(Result.success());

                }

                @SuppressLint("RestrictedApi")
                @Override
                public void onError(String errorMessage) {
                    future.set(Result.failure());
                }
            });
        } catch (Exception e) {
            future.set(Result.retry());
        }
        return future;
    }
}
