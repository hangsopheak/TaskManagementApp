package com.example.taskmanagement;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.taskmanagement.databinding.ActivityMainBinding;
import com.example.taskmanagement.R;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_tasks) {
                LoadFragment(new TasksFragment());
            } else if (itemId == R.id.nav_categories) {
                LoadFragment(new CategoriesFragment());
            } else if (itemId == R.id.nav_calendar) {
                LoadFragment(new CalendarFragment());
            } else if (itemId == R.id.nav_setting) {
                LoadFragment(new SettingFragment());
            } else {
                return false; // Return false if no valid ID is matched
            }
            return true;
        });


        binding.bottomNavigation.setSelectedItemId(R.id.nav_tasks);
    }

    private void LoadFragment(Fragment fragment) {
        var fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());

        if(existingFragment == null){
            fragmentTransaction.replace(binding.fragmentContainer.getId(), fragment);

        }else{
            fragmentTransaction.replace(binding.fragmentContainer.getId(), existingFragment);
        }
        
        fragmentTransaction.replace(binding.fragmentContainer.getId(), fragment);
        fragmentTransaction.commit();
    }


}