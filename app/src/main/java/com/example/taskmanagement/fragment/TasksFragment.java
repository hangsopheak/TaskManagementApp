package com.example.taskmanagement.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.taskmanagement.adapter.TaskAdapter;
import com.example.taskmanagement.databinding.FragmentTasksBinding;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.IApiCallback;
import com.example.taskmanagement.repository.TaskRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class TasksFragment extends Fragment {


    FragmentTasksBinding binding;
    private TaskAdapter taskAdapter;
    private int currentPage = 1;
    private boolean isLoading;
    private FirebaseAuth mAuth;

    private static final int PRE_LOAD_ITEMS = 2;
    private TaskRepository taskRepository;
    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rcvTasks.setLayoutManager(layoutManager);
        taskRepository = new TaskRepository();
        taskAdapter = new TaskAdapter();
        mAuth = FirebaseAuth.getInstance();
        binding.rcvTasks.setAdapter(taskAdapter);


        binding.rcvTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + PRE_LOAD_ITEMS)) {
                    loadTasks(false);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;
        loadTasks(true);
    }

    private void loadTasks(boolean reset) {
        isLoading = true;
        showProgressBar();
        //String currentUserId = "1249588e-aea4-4a9e-930d-0778c8669364";
        String currentUserId = mAuth.getCurrentUser().getUid();
        taskRepository.getTasks(currentPage, currentUserId, new IApiCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if(!tasks.isEmpty()){
                    if (reset) {
                        taskAdapter.setTasks(tasks);
                    } else {
                        taskAdapter.addTasks(tasks);
                    }
                    currentPage++;
                }
                isLoading = false;
                hideProgressBar();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                isLoading = false;
                hideProgressBar();
            }

        });
    }

    private void showProgressBar() {
        binding.tasksProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.tasksProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}