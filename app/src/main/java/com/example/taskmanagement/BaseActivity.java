package com.example.taskmanagement;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanagement.util.LocaleHelper;
import com.example.taskmanagement.util.ThemeHelper;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        // Apply the locale using LocaleHelper
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguageFromPreferences(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.setTheme(this, ThemeHelper.getThemeFromPreferences(this));
    }


}
