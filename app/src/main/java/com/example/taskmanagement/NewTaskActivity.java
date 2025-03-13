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
import com.example.taskmanagement.service.CategoryRepository;
import com.example.taskmanagement.service.IApiCallback;
import com.example.taskmanagement.service.TaskRepository;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {

    private TaskRepository taskRepository;
    private CategoryRepository categoryRepository;
    ActivityNewTaskBinding binding;
    Category selectedCategory;
    MaterialDatePicker datePicker;
    MaterialTimePicker timePicker;


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

        taskRepository = new TaskRepository();
        categoryRepository = new CategoryRepository();
        loadCategories();

        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).build();


        binding.tvNewTaskDuedate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    NewTaskActivity.this.showDatePicker();
                }
            }
        });




        binding.tvNewTaskDuetime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    NewTaskActivity.this.showTimePicker();
                }

            }
        });



        datePicker.addOnPositiveButtonClickListener(selection -> {
            // selection is a Long representing the selected date in milliseconds
            // Convert it to a readable date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(selection);

            // Set the selected date to your TextView or wherever you need it
            binding.tvNewTaskDuedate.setText(formattedDate);
        });

        timePicker.addOnPositiveButtonClickListener(selection -> {
            binding.tvNewTaskDuetime.setText(String.format("%02d:%02d", timePicker.getHour() , timePicker.getMinute()));
        });



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
    }

    private void showTimePicker() {
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void showDatePicker() {

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void loadCategories() {
        categoryRepository.getCategories(new IApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> categories) {
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_item, categories);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Set adapter to Spinner
                binding.spNewTaskCategory.setAdapter(adapter);
            }
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getParent(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}