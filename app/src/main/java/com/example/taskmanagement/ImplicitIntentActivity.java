package com.example.taskmanagement;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanagement.databinding.ActivityImplicitIntentBinding;

public class ImplicitIntentActivity extends AppCompatActivity {
    private ActivityImplicitIntentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityImplicitIntentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}