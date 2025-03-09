package com.example.taskmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskmanagement.adapter.TaskAdapter;
import com.example.taskmanagement.databinding.FragmentTasksBinding;
import com.example.taskmanagement.service.TaskData;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private TaskAdapter taskAdapter;

    public TasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate(inflater, container, false);

        binding.rcTasksList.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(TaskData.SAMPLE_TASKS);
        binding.rcTasksList.setAdapter(taskAdapter);
        return binding.getRoot();
    }
}