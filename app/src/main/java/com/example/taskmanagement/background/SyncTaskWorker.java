package com.example.taskmanagement.background;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.example.taskmanagement.repository.IApiCallback;
import com.example.taskmanagement.repository.TaskRepository;
import com.google.common.util.concurrent.ListenableFuture;

public class SyncTaskWorker extends ListenableWorker {


    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public SyncTaskWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            try {
                TaskRepository repository = new TaskRepository(getApplicationContext());

                repository.syncTasks(new IApiCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Broadcast when sync is completed
                        Intent intent = new Intent("com.example.taskmanagement.SYNC_COMPLETED");
                        LocalBroadcastManager.getInstance(getApplicationContext())
                                .sendBroadcast(intent);

                        completer.set(Result.success());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        completer.set(Result.retry());
                    }
                });

            } catch (Exception e) {
                completer.set(Result.failure());
            }

            return "SyncTaskWorker";
        });
    }
}