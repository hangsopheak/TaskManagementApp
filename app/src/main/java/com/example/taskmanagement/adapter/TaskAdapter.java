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
import com.example.taskmanagement.service.TaskData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.binding.tvTaskTitle.setText(task.getTitle());
        holder.binding.tvTaskDescription.setText(task.getDescription());
        holder.binding.tvDueDate.setText(task.getFormattedDueDate());
        holder.binding.chCategory.setText(task.getCategory());
        String colorCode = TaskData.getCategoryColor(task.getCategory());
        holder.binding.chCategory.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor(colorCode)));

        // Handle switch toggle
        holder.binding.swComplete.setChecked(task.isCompleted());
        holder.binding.swComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
        });

        String status = getStatus(task);
        holder.binding.tvStatus.setText(status);
        int colorResId = getStatusColor(status); // Get color based on status

        // Change the background color
        GradientDrawable drawable = (GradientDrawable) holder.binding.tvStatus.getBackground();
        drawable.setColor(ContextCompat.getColor(holder.itemView.getContext(), colorResId));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailTaskActivity.class);
            intent.putExtra("expenseId", task.getId()); // Pass only the ID
            holder.itemView.getContext().startActivity(intent);
        });
    }

    private String getStatus(Task task) {
        if (task.isCompleted()) {
            return "Completed";
        }

        if(task.getDueDate().before(new Date()))
        {
            return "To Do";
        }

        return "Overdue";
    }



    @Override
    public int getItemCount() {
        return taskList.size();
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

    static class TaskViewHolder extends RecyclerView.ViewHolder{
        protected ItemTaskBinding binding;
        public TaskViewHolder(@NonNull ItemTaskBinding  binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
