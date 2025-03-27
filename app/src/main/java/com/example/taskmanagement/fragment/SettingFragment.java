package com.example.taskmanagement.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.taskmanagement.R;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.IApiCallback;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.util.LocaleHelper;
import com.example.taskmanagement.util.ThemeHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SettingFragment extends PreferenceFragmentCompat {

    private static final String PREF_LANGUAGE = "app_language";
    private static final String PREF_THEME = "app_theme";

    private int currentPage = 1;
    private TaskRepository repository;
    private FirebaseAuth mAuth;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        // Set the preferences from XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        repository = new TaskRepository();
        mAuth = FirebaseAuth.getInstance();

        // Language Preference Listener
        ListPreference languagePreference = findPreference(PREF_LANGUAGE);
        if (languagePreference != null) {
            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                LocaleHelper.setLocale(requireActivity(), newValue.toString());
                requireActivity().recreate();
                return true;
            });
        }

        // Theme Preference Listener
        ListPreference themePreference = findPreference(PREF_THEME);
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                ThemeHelper.setTheme(requireActivity(), newValue.toString());
                requireActivity().recreate();
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

    }

    private void backupTasks() {
        Toast.makeText(getContext(), R.string.loading_tasks, Toast.LENGTH_SHORT).show();
        String currentUserId = mAuth.getCurrentUser().getUid();
        repository.getTasks(currentPage, currentUserId, new IApiCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {

                try {
                    // Generate a unique filename with timestamp
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String filename = "tasks_backup_" + timestamp + ".json";

                    // Convert tasks to JSON
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonTasks = gson.toJson(tasks);

                    // Write serialized json to file
                    try (FileOutputStream fos = requireContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
                        fos.write(jsonTasks.getBytes());
                        Toast.makeText(getContext(), "Tasks backed up to " + filename, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error backing up tasks " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }


}