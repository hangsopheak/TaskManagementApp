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
    private TaskRepository repository;
    private boolean isLoading = false;  // Prevents loading the same page multiple times while still fetching
    private int currentPage = 1;        // Keeps track of which page of data we are loading (pagination)
    private static final int PRE_LOAD_ITEMS = 1;  // How early to trigger loading before the user reaches the bottom
    private FirebaseAuth mAuth;

    private TaskAdapter taskAdapter;

    public TasksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rcvTasks.setLayoutManager(layoutManager);
        taskAdapter = new TaskAdapter();
        binding.rcvTasks.setAdapter(taskAdapter);

        repository = new TaskRepository();
        mAuth = FirebaseAuth.getInstance();   // Firebase Authentication instance (to get current logged user)

        // ---------------------- INFINITE SCROLL LISTENER ----------------------
        // This listener is triggered every time the user scrolls.
        // We use it to detect when the user reaches near the bottom of the list,
        // then we load the next page automatically.
        binding.rcvTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();               // Total items currently loaded
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition(); // Last visible index on screen

                // If not already loading AND user has scrolled close to the bottom,
                // then load the next page.
                if (!isLoading && totalItemCount <= (lastVisibleItem + PRE_LOAD_ITEMS)) {
                    loadTasks(false);   // false = append new items instead of resetting
                }
            }
        });
        // ----------------------------------------------------------------------

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;    // Reset pagination when returning to this screen
        loadTasks(true);    // true = clear old data and reload from page 1
    }

    private void loadTasks(boolean reset)  {

        isLoading = true;     // Block other load requests until this one finishes
        showProgressBar();

        // Get the currently logged-in user ID from Firebase
       // String currentUserId = mAuth.getCurrentUser().getUid();
        String currentUserId = "1249588e-aea4-4a9e-930d-0778c8669364";

        // ---------------------- ASYNC API CALL ----------------------
        // This calls the repository to fetch tasks from network/database asynchronously.
        // The result will come back through the callback functions below.
        repository.getTasks(currentPage, currentUserId, new IApiCallback<List<Task>>() {

            @Override
            public void onSuccess(List<Task> tasks) {

                // If the API returns data
                if (!tasks.isEmpty()) {

                    if (reset) {
                        // Replace entire list (used on first load or when user returns to screen)
                        taskAdapter.setTasks(tasks);
                    } else {
                        // Add new page of results at the bottom (infinite scroll)
                        taskAdapter.addTasks(tasks);
                    }

                    // Move to the next page for next load
                    currentPage++;
                }

                isLoading = false;    // Allow future loads
                hideProgressBar();
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error and notify user
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                isLoading = false;
                hideProgressBar();
            }

        });
        // ------------------------------------------------------------
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