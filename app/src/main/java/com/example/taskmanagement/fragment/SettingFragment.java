package com.example.taskmanagement.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.taskmanagement.R;
import com.example.taskmanagement.util.LocaleHelper;
import com.example.taskmanagement.util.ThemeHelper;


public class SettingFragment extends PreferenceFragmentCompat {

    private static final String PREF_LANGUAGE = "app_language";
    private static final String PREF_THEME = "app_theme";

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        // Set the preferences from XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

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

    }


}