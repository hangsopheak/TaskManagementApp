package com.example.taskmanagement;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanagement.databinding.ActivityDetailTaskBinding;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.service.TaskData;

public class DetailTaskActivity extends AppCompatActivity {

    private ActivityDetailTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetailTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String TaskId = getIntent().getStringExtra("TaskId");
        Task task = TaskData.getTaskById(TaskId);
        binding.tvTaskDetailTitle.setText(task.getTitle());
        binding.tvTaskDetailDescription.setText(task.getDescription());
        binding.tvTaskDetailCategory.setText(task.getCategoryId());
        binding.tvTaskDetailDueDate.setText(task.getFormattedDueDate());
        binding.swTaskDetailComplete.setChecked(task.isCompleted());

    }
}