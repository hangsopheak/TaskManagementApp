package com.example.taskmanagement.repository;

import android.content.Context;

import com.example.taskmanagement.dao.AppDatabase;
import com.example.taskmanagement.dao.TaskDao;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.util.NetworkUtil;
import com.example.taskmanagement.util.RetrofitClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskRepository {
    private TaskService taskService;
    private static final int PAGE_SIZE = 6;
    private Context context;
    private TaskDao taskDao;


    public TaskRepository(Context context) {

        this.context = context;
        AppDatabase db = AppDatabase.getInstance(context);
        this.taskDao = db.taskDao();
        taskService = RetrofitClient.getClient().create(TaskService.class);
    }

    public void getTasks(int page, String createdBy, final IApiCallback<List<Task>> callback) {
        Call<List<Task>> call = taskService.getTasks(page, PAGE_SIZE, createdBy);

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createTask(Task task, final IApiCallback<Task> callback) {
        if (NetworkUtil.isNetworkAvailable(context)) {

            taskService.createTask(task).enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError(getErrorMessage(response));
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        }else{
            saveTaskLocally(task);
            callback.onError("No network. Task saved locally.");
        }
    }

    public void syncTasks(final IApiCallback<String> callback) {
        // Background thread to avoid UI freeze
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> unsyncedTasks = taskDao.getUnsyncedTasks();

            if (unsyncedTasks.isEmpty()) {
                callback.onSuccess("All tasks are already synced.");
                return;
            }

            // Atomic counters must be used since network callbacks run on different threads
            AtomicInteger remainingTasks = new AtomicInteger(unsyncedTasks.size());
            AtomicBoolean hasError = new AtomicBoolean(false);

            for (Task task : unsyncedTasks) {
                taskService.createTask(task).enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        // Only mark as synced if server confirms success
                        if (response.isSuccessful()) {
                            markTaskAsSynced(task);
                        } else {
                            hasError.set(true);  // Server responded but with error (e.g. 400)
                        }
                        checkSyncCompletion(callback, remainingTasks, hasError);
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {
                        hasError.set(true);  // Network failure (no connection, timeout)
                        checkSyncCompletion(callback, remainingTasks, hasError);
                    }
                });

                // API server can't accept burst request, therefore sleep for 1 second per request
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void markTaskAsSynced(final Task task) {
        Executors.newSingleThreadExecutor().execute(() -> {
            taskDao.markTaskAsSynced(task.getId()); // Run on a background thread
        });
    }

    private void checkSyncCompletion(IApiCallback<String> callback, AtomicInteger remainingTasks, AtomicBoolean hasError) {
        if (remainingTasks.decrementAndGet() == 0) {
            if (hasError.get()) {
                callback.onError("Some tasks failed to sync.");
            } else {
                callback.onSuccess("All tasks synced successfully.");
            }
        }
    }

    private void saveTaskLocally(Task task) {
        task.setSynced(false);
        Executors.newSingleThreadExecutor().execute(() -> {
            taskDao.insertTask(task); // Run on a background thread
        });
    }

    public void deleteTask(String taskId, final IApiCallback<String> callback) {
        taskService.deleteTask(taskId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess("Task deleted successfully");
                } else {
                    callback.onError(getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private String getErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                return "Error: " + response.code() + " - " + response.errorBody().string();
            }
        } catch (IOException e) {
            return "Error: " + response.code() + " (failed to read error body)";
        }
        return "Unknown error: " + response.code();
    }

}

