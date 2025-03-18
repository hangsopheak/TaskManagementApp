package com.example.taskmanagement;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityNewTaskBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {


    ActivityNewTaskBinding binding;
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

        // TODO: Load Categories
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
        binding.tvNewTaskDuedate.setOnClickListener(view -> showDatePicker());
        binding.tvNewTaskDuetime.setOnClickListener(view -> showTimePicker());

    }


}