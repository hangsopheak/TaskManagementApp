package com.example.taskmanagement;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagement.databinding.ActivityBackgroundDemoBinding;

public class ActivityBackgroundDemo extends AppCompatActivity {

    private static final String TAG = "BackgroundDemo";
    private ActivityBackgroundDemoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBackgroundDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}