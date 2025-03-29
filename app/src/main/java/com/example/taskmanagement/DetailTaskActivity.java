package com.example.taskmanagement;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanagement.databinding.ActivityDetailTaskBinding;

public class DetailTaskActivity extends AppCompatActivity {

    private ActivityDetailTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String TaskId = getIntent().getStringExtra("TaskId");
//        Task task = TaskData.getTaskById(TaskId);
//        binding.tvTaskDetailTitle.setText(task.getTitle());
//        binding.tvTaskDetailDescription.setText(task.getDescription());
//        binding.tvTaskDetailCategory.setText(task.getCategory());
//        binding.tvTaskDetailDueDate.setText(task.getFormattedDueDate());
//        binding.swTaskDetailComplete.setChecked(task.isCompleted());

    }
}