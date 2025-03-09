package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TaskData {
    public static final Map<String, String> CATEGORY_COLORS = new HashMap<>();
    public static final List<Task> SAMPLE_TASKS = new ArrayList<>();

    static {
        CATEGORY_COLORS.put("Work", "#FF9800");      // Orange
        CATEGORY_COLORS.put("Shopping", "#4CAF50");  // Green
        CATEGORY_COLORS.put("Fitness", "#03A9F4");   // Blue
        CATEGORY_COLORS.put("Personal", "#9C27B0");  // Purple
        CATEGORY_COLORS.put("Health", "#E91E63");    // Pink
        CATEGORY_COLORS.put("Family", "#795548");    // Brown
        CATEGORY_COLORS.put("Finance", "#FF5722");   // Deep Orange
        CATEGORY_COLORS.put("Learning", "#009688");  // Teal
        CATEGORY_COLORS.put("Writing", "#673AB7");   // Deep Purple
        CATEGORY_COLORS.put("Wellness", "#8BC34A");  // Light Green
        CATEGORY_COLORS.put("Travel", "#FFEB3B");    // Yellow
        CATEGORY_COLORS.put("Hobby", "#F44336");     // Red
    }
    
    static {
        SAMPLE_TASKS.add(new Task("Finish Project Report", "Complete the final report for the client", getRandomDateWithinRange(), "Work", false));
        SAMPLE_TASKS.add(new Task("Buy Groceries", "Get milk, eggs, and vegetables", getRandomDateWithinRange(), "Shopping", false));
        SAMPLE_TASKS.add(new Task("Morning Workout", "Do a 30-minute run", getRandomDateWithinRange(), "Fitness", true));
        SAMPLE_TASKS.add(new Task("Read a Book", "Finish reading 'Atomic Habits'", getRandomDateWithinRange(), "Personal", false));
        SAMPLE_TASKS.add(new Task("Doctor Appointment", "Visit Dr. Smith at 3 PM", getRandomDateWithinRange(), "Health", false));
        SAMPLE_TASKS.add(new Task("Prepare Presentation", "Create slides for Monday's meeting", getRandomDateWithinRange(), "Work", false));
        SAMPLE_TASKS.add(new Task("Call Mom", "Catch up with mom on the phone", getRandomDateWithinRange(), "Family", true));
        SAMPLE_TASKS.add(new Task("Pay Bills", "Pay electricity and internet bills", getRandomDateWithinRange(), "Finance", false));
        SAMPLE_TASKS.add(new Task("Organize Desk", "Clean and organize workspace", getRandomDateWithinRange(), "Personal", true));
        SAMPLE_TASKS.add(new Task("Watch a Tech Talk", "Watch latest AI advancements on YouTube", getRandomDateWithinRange(), "Learning", false));
        SAMPLE_TASKS.add(new Task("Prepare Lunch", "Cook a healthy meal", getRandomDateWithinRange(), "Personal", true));
        SAMPLE_TASKS.add(new Task("Team Meeting", "Discuss sprint progress with team", getRandomDateWithinRange(), "Work", false));
        SAMPLE_TASKS.add(new Task("Write Blog Post", "Draft an article on productivity tips", getRandomDateWithinRange(), "Writing", false));
        SAMPLE_TASKS.add(new Task("Laundry", "Wash and fold clothes", getRandomDateWithinRange(), "Personal", false));
        SAMPLE_TASKS.add(new Task("Meditation", "15 minutes of mindfulness meditation", getRandomDateWithinRange(), "Wellness", true));
        SAMPLE_TASKS.add(new Task("Fix Bug in Code", "Resolve issue in the API endpoint", getRandomDateWithinRange(), "Work", false));
        SAMPLE_TASKS.add(new Task("Plan Weekend Trip", "Research places to visit", getRandomDateWithinRange(), "Travel", true));
        SAMPLE_TASKS.add(new Task("Practice Guitar", "Learn new chords", getRandomDateWithinRange(), "Hobby", false));
        SAMPLE_TASKS.add(new Task("Schedule Dentist Appointment", "Book a check-up for next week", getRandomDateWithinRange(), "Health", false));
        SAMPLE_TASKS.add(new Task("Back Up Files", "Save important files to cloud storage", getRandomDateWithinRange(), "Work", true));
    }



    public static Task getTaskById(String taskId) {
        return SAMPLE_TASKS.stream().filter(x -> x.getId().equals(taskId)).findFirst().orElse(null);
    }

    public static String getCategoryColor(String category) {
        return CATEGORY_COLORS.getOrDefault(category, "#000000"); // Default to black
    }


    public static Date getRandomDateWithinRange() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Generate a random number between -3 and 3 (inclusive)
        Random random = new Random();
        int randomDays = random.nextInt(7) - 3; // Range: -3 to +3

        // Add the random number of days to the current date
        calendar.add(Calendar.DAY_OF_YEAR, randomDays);
        return calendar.getTime();
    }
}
