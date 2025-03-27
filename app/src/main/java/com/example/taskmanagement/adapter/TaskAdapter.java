package com.example.taskmanagement.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagement.DetailTaskActivity;
import com.example.taskmanagement.R;
import com.example.taskmanagement.databinding.ItemTaskBinding;
import com.example.taskmanagement.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> tasks;

    public TaskAdapter() {
        this.tasks = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.binding.tvTaskTitle.setText(task.getTitle());
        holder.binding.tvTaskDescription.setText(task.getDescription());
        holder.binding.tvDueDate.setText(task.getFormattedDueDate());
        holder.binding.chCategory.setText(task.getCategory().getName());
        String colorCode = task.getCategory().getColorCode();
        holder.binding.chCategory.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(colorCode)));

        holder.binding.swComplete.setChecked(task.isCompleted());

        String status = getStatus(task);
        holder.binding.tvStatus.setText(status);
        int colorResId = getStatusColor(status);
        GradientDrawable drawable = (GradientDrawable) holder.binding.tvStatus.getBackground();
        drawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), colorResId));

        holder.itemView.setOnClickListener(view -> {
           Intent intent = new Intent(holder.itemView.getContext(), DetailTaskActivity.class);
           intent.putExtra("TaskId", task.getId());
           holder.itemView.getContext().startActivity(intent);
        });
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "To Do":
                return R.color.todoColor;
            case "Completed":
                return R.color.completeColor;
            case "Overdue":
                return R.color.overdueColor;
            default:
                return R.color.todoColor;
        }
    }

    private String getStatus(Task task) {
        if (task.isCompleted()) {
            return "Completed";
        }

        if(task.getDueDate().after(new Date()))
        {
            return "To Do";
        }

        return "Overdue";
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // allow user to initialize the tasks
    public void setTasks(List<Task> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    // allow user to append new tasks when receiving new tasks from APIs
    public void addTasks(List<Task> newTasks) {
        int startPosition = tasks.size();
        tasks.addAll(newTasks);
        notifyItemRangeInserted(startPosition, newTasks.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        protected ItemTaskBinding binding;
        public ViewHolder(@NonNull ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
