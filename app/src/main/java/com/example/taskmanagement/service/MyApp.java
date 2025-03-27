package com.example.taskmanagement.service;

import android.app.Application;

import com.example.taskmanagement.fragment.SettingFragment;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SettingFragment.applyAppSettings(getApplicationContext());
    }



}
