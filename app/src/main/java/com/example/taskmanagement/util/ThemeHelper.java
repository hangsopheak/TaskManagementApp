package com.example.taskmanagement.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class ThemeHelper {
    private static final String THEME_LIGHT_VALUE = "light";
    private static final String THEME_DARK_VALUE = "dark";
    private static final String PREF_THEME = "app_theme";

    public static void setTheme(Context context, String theme) {
        // Save theme preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_THEME, theme).apply();

        // Apply theme
        changeTheme(context, theme);
    }

    // Method to change theme with persistent storage
    public static void changeTheme(Context context, String theme) {

        // Apply theme
        switch (theme) {
            case THEME_LIGHT_VALUE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK_VALUE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    public static String getThemeFromPreferences(Context context) {
        return androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_THEME, THEME_LIGHT_VALUE);
    }

}