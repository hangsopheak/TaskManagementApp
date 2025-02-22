package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityRegisterBinding;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.IUserService;
import com.example.taskmanagement.service.MyApp;
import com.example.taskmanagement.service.UserService;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private IUserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userService =  ((MyApp) getApplication()).getUserService();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.tvAlreadyHaveAccount.setOnClickListener(v -> {
            onAlreadyHaveAccountClicked();
        });

        binding.btnSignUp.setOnClickListener(v -> {
            onSignUpClicked();
        });
    }

    private void onSignUpClicked() {
        String email = binding.etNewEmail.getText().toString();
        String password = binding.etNewPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        try {
            userService.register(email, password, confirmPassword);

            List<User> users = userService.getAllUsers();
            users.forEach(user -> {
                Log.d("RegisterActivity", "User: " + user.getEmail() + ", Password: " + user.getPassword());
            });

            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("email", email);
            loginIntent.putExtra("password", password);
            setResult(RESULT_OK, loginIntent);
            finish();
        }catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onAlreadyHaveAccountClicked() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}