package com.example.taskmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityNewTaskBinding;
import com.example.taskmanagement.model.Category;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.repository.CategoryRepository;
import com.example.taskmanagement.repository.IApiCallback;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.util.DateConverter;
import com.example.taskmanagement.util.NetworkUtil;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {


    ActivityNewTaskBinding binding;
    MaterialDatePicker datePicker;
    MaterialTimePicker timePicker;
    CategoryRepository categoryRepository;
    TaskRepository taskRepository;
    Category selectedCategory;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNewTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        categoryRepository = new CategoryRepository();
        taskRepository = new TaskRepository(this);
        mAuth = FirebaseAuth.getInstance();
        loadCategories();
        initDatePicker();
        initTimePicker();
        setupListeners();

    }

    private void initTimePicker() {
        timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build();
        timePicker.addOnPositiveButtonClickListener(selection -> {
            binding.tvNewTaskDuetime.setText(String.format("%02d:%02d", timePicker.getHour() , timePicker.getMinute()));
        });

    }

    private void loadCategories() {
        showProgressBar();
        categoryRepository.getCategories(new IApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                hideProgressBar();
                if (categories == null || categories.isEmpty()) {
                    Toast.makeText(NewTaskActivity.this, "No categories found", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(NewTaskActivity.this,
                        android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spNewTaskCategory.setAdapter(adapter);
            }
            @Override
            public void onError(String errorMessage) {
                hideProgressBar();
                Toast.makeText(NewTaskActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                binding.spNewTaskCategory.setEnabled(false);

            }
        });
    }

    private void initDatePicker() {
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // selection is a Long representing the selected date in milliseconds
            // Convert it to a readable date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selection);

            // Set the selected date to your TextView or wherever you need it
            binding.tvNewTaskDuedate.setText(formattedDate);
        });
    }

    private void showTimePicker() {
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void showDatePicker() {

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }



    private void showProgressBar() {
        binding.tasksProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.tasksProgressBar.setVisibility(View.GONE);
    }

    private void setupListeners(){
        binding.btNewTask.setOnClickListener(view -> addTask());

        // Handle item selection
        binding.spNewTaskCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (Category) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.tvNewTaskDuedate.setOnClickListener(view -> showDatePicker());
        binding.tvNewTaskDuetime.setOnClickListener(view -> showTimePicker());

    }

    private void addTask() {
        showProgressBar();
        String title = binding.etNewTaskTitle.getText().toString();
        String description = binding.etNewTaskDescription.getText().toString();
        String dueDate = binding.tvNewTaskDuedate.getText().toString();
        String dueTime = binding.tvNewTaskDuetime.getText().toString();

        if (title.isEmpty() || dueDate.isEmpty() || dueTime.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(title, description, DateConverter.convertToDate(dueDate, dueTime), selectedCategory.getId(), false , mAuth.getCurrentUser().getUid(), new java.util.Date());
        taskRepository.createTask(task, new IApiCallback<Task>() {
            @Override
            public void onSuccess(Task result) {
                hideProgressBar();
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                hideProgressBar();
                Toast.makeText(NewTaskActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

            }

        });
    }

}