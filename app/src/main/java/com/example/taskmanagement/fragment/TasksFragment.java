package com.example.taskmanagement.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapter.TaskListAdapter;
import com.example.taskmanagement.databinding.FragmentTasksBinding;
import com.example.taskmanagement.service.TaskData;


public class TasksFragment extends Fragment {

    FragmentTasksBinding binding;
    TaskListAdapter taskAdapter;

    public TasksFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /// Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate(inflater, container, false);

        binding.rcTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskListAdapter(TaskData.SAMPLE_TASKS);
        binding.rcTasks.setAdapter(taskAdapter);
        return binding.getRoot();
    }
}