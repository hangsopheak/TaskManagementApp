package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.Fragment;

import com.example.taskmanagement.databinding.ActivityMainBinding;
import com.example.taskmanagement.fragment.CalendarFragment;
import com.example.taskmanagement.fragment.CategoriesFragment;
import com.example.taskmanagement.fragment.SettingFragment;
import com.example.taskmanagement.fragment.TasksFragment;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.signout) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId == R.id.newtask) {
            Intent intent = new Intent(this, NewTaskActivity.class);
            startActivity(intent);
            return true;
        }else if (itemId == R.id.implicit_intent) {
            Intent intent = new Intent(this, ImplicitIntentActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // For explicit Toolbar from layout
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setTitle("");

        mAuth = FirebaseAuth.getInstance();


        binding.bottomNavigation.setOnItemSelectedListener(item -> {
           int itemId = item.getItemId();
           if (itemId == R.id.nav_tasks) {
               LoadFragment(new TasksFragment());
           }else if(itemId == R.id.nav_categories){
               LoadFragment(new CategoriesFragment());
           }else if(itemId == R.id.nav_calendar){
               LoadFragment(new CalendarFragment());
           }
           else if(itemId == R.id.nav_setting){
               LoadFragment(new SettingFragment());
           }
           else{
               return false;
           }
            return true;
        });

        // To ensure the selected bottom navigation item is always visible and stay where it was when activity recreated
        if(savedInstanceState == null){
            binding.bottomNavigation.setSelectedItemId(R.id.nav_tasks);
        }else{
            binding.bottomNavigation.setSelectedItemId(savedInstanceState.getInt("selectedItemId"));
        }
    }

    private void LoadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}