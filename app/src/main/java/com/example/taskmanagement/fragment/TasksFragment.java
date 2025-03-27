package com.example.taskmanagement.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapter.TaskAdapter;
import com.example.taskmanagement.databinding.FragmentTasksBinding;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.IApiCallback;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.util.NetworkUtil;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class TasksFragment extends Fragment {


    FragmentTasksBinding binding;
    private TaskRepository repository;
    private int currentPage = 1;
    private boolean isLoading = false;
    private static final int PRE_LOAD_ITEMS = 1;
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
        repository = new TaskRepository(requireContext());
        mAuth = FirebaseAuth.getInstance();

        binding.rcvTasks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + PRE_LOAD_ITEMS)) {
                    loadTasks();
                }
            }
        });
        setSwipeToDelete();
        return binding.getRoot();
    }

    private void setSwipeToDelete() {
        taskAdapter.setOnTaskDeleteListener(new TaskAdapter.OnTaskDeleteListener() {
            @Override
            public void onTaskDelete(Task task) {
                deleteTask(task.getId());
            }
        });

        // swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                taskAdapter.removeTask(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(binding.rcvTasks);
    }

    private void deleteTask(String id) {
        showProgressBar();
        repository.deleteTask(id, new IApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                hideProgressBar();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgressBar();
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks()  {
        isLoading = true;
        showProgressBar();
        //String currentUserId = "1249588e-aea4-4a9e-930d-0778c8669364";
        String currentUserId = mAuth.getCurrentUser().getUid();
        repository.getTasks(currentPage, currentUserId, new IApiCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> tasks) {
                if(!tasks.isEmpty()){
                    taskAdapter.addTasks(tasks);
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
}
