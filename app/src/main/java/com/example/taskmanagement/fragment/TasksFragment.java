package com.example.taskmanagement.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapter.TaskAdapter;
import com.example.taskmanagement.databinding.FragmentTasksBinding;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.service.IApiCallback;
import com.example.taskmanagement.service.TaskData;
import com.example.taskmanagement.service.TaskRepository;
import com.google.android.material.carousel.CarouselLayoutManager;

import java.util.List;


public class TasksFragment extends Fragment {


    FragmentTasksBinding binding;
    private TaskAdapter taskAdapter;
    private TaskRepository repository;
    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);

        binding.rcvTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTasks();
        return binding.getRoot();
    }

    private void loadTasks() {
        repository = new TaskRepository();
        repository.getTasks(new IApiCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskAdapter = new TaskAdapter(tasks);
                binding.rcvTasks.setAdapter(taskAdapter);

            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}