package com.example.taskmanagement.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.AppDatabase;
import com.example.taskmanagement.dao.TaskDao;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.IApiCallback;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.util.NetworkUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SettingFragment extends PreferenceFragmentCompat {


    private static final String TAG = "SettingsFragment";
    private static final String PREF_LANGUAGE = "app_language";
    private static final String PREF_THEME = "app_theme";
    private static final String PREF_REMINDER_TIME = "default_reminder_time";
    private int currentPage = 1;
    private TaskRepository repository;
    private FirebaseAuth mAuth;
    private TaskDao taskDao;
    private int countUnsyncedTasks;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Set the preferences from XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        repository = new TaskRepository(requireContext());
        taskDao = AppDatabase.getInstance(requireContext()).taskDao();
        mAuth = FirebaseAuth.getInstance();

        updateUnsyncedTasksCount();

        // Language Preference Listener
        ListPreference languagePreference = findPreference(PREF_LANGUAGE);
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                saveLanguage(requireActivity(), newValue.toString());
                changeLanguage(requireActivity(), newValue.toString());
                requireActivity().recreate();
                return true;
            });
        }

        // Theme Preference Listener
        ListPreference themePreference = findPreference(PREF_THEME);
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                saveTheme(requireActivity(), newValue.toString());
                changeTheme(requireActivity(), newValue.toString());
                requireActivity().recreate();
                return true;
            });
        }

        // Reminder Time Listener
        ListPreference reminderPreference = findPreference(PREF_REMINDER_TIME);
        if (reminderPreference != null) {
            reminderPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                changeReminderTime(requireActivity(), newValue.toString());
                return true;
            });
        }

        // Backup Tasks Listener
        Preference backupPreference = findPreference("backup_tasks");
        if (backupPreference != null) {
            backupPreference.setOnPreferenceClickListener(preference -> {
                backupTasks();
                return true;

            });
        }

        Preference unsyncedTasksPreference = findPreference("unsynced_tasks");
        if (unsyncedTasksPreference != null) {
            unsyncedTasksPreference.setOnPreferenceClickListener(preference -> {
                if(countUnsyncedTasks > 0 && NetworkUtil.isNetworkAvailable(requireContext())){
                    showSyncConfirmationDialog();
                }

                return true;
            });
        }
    }

    private void updateUnsyncedTasksCount() {
        // Create a single thread executor
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            countUnsyncedTasks = taskDao.getUnsyncedTasks().size();

            // Post result to main thread
            handler.post(() -> {
                Toast.makeText(requireContext(), "Count: " + countUnsyncedTasks, Toast.LENGTH_SHORT).show();
                Preference unsyncedTasksPreference = findPreference("unsynced_tasks");
                if (unsyncedTasksPreference != null) {
                    unsyncedTasksPreference.setSummary(getString(R.string.unsynced_tasks_summary, countUnsyncedTasks));
                }
            });
        });
    }

    private void showSyncConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sync Tasks")
                .setMessage("You have " + countUnsyncedTasks + " unsynced tasks. Do you want to sync now?")
                .setPositiveButton("Sync", (dialog, which) -> syncUnsyncedTasks())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void syncUnsyncedTasks() {
        repository.syncTasks(new IApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                updateUnsyncedTasksCount();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                updateUnsyncedTasksCount();
            }
        });
    }

    private void backupTasks()  {
        Toast.makeText(getContext(), R.string.loading_tasks, Toast.LENGTH_SHORT).show();
        //String currentUserId = "1249588e-aea4-4a9e-930d-0778c8669364";
        String currentUserId = mAuth.getCurrentUser().getUid();
        repository.getTasks(currentPage, currentUserId, new IApiCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {

                try {
                    // Generate a unique filename with timestamp
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String filename = "tasks_backup_" + timestamp + ".json";

                    // Check if file already exists and handle accordingly
                    if (isFileExists(filename)) {
                        filename = "tasks_backup_" + System.currentTimeMillis() + ".json";
                    }

                    // Convert tasks to JSON
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonTasks = gson.toJson(tasks);

                    try (FileOutputStream fos = requireContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
                        fos.write(jsonTasks.getBytes());
                        Toast.makeText(getContext(), "Tasks backed up to " + filename, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error backing up tasks " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            // Check if file exists in internal storage
            private boolean isFileExists(String filename) {
                try {
                    File file = new File(requireContext().getFilesDir(), filename);
                    return file.exists();
                } catch (Exception e) {
                    Log.e("FileCheck", "Error checking file existence", e);
                    return false;
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void saveTheme(Context context, String theme) {
        // Save theme preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_THEME, theme).apply();
    }

    private void saveLanguage(Context context, String languageCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_LANGUAGE, languageCode).apply();
    }

    // Method to change language with persistent storage
    public static void changeLanguage(Context context, String languageCode) {
        // Set the locale
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        // Create a new configuration with the selected locale
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        // Apply the configuration to the context
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    // Method to change theme with persistent storage
    public static void changeTheme(Context context, String theme) {

        // Apply theme
        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    // Method to change reminder time with persistent storage
    public static void changeReminderTime(Context context, String reminderTime) {
        // Save reminder time preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_REMINDER_TIME, reminderTime).apply();

        Log.d(TAG, "Default reminder time set to: " + reminderTime + " minutes before deadline");
    }

    // Method to apply saved settings on app start
    public static void applyAppSettings(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // Apply Language
        String languageCode = prefs.getString(PREF_LANGUAGE, "en");
        changeLanguage(context, languageCode);

        // Apply Theme
        String theme = prefs.getString(PREF_THEME, "light");
        changeTheme(context, theme);
    }

}


