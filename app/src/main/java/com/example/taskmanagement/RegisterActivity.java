package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityRegisterBinding;
import com.example.taskmanagement.service.IUserService;
import com.example.taskmanagement.service.MyApp;
import com.example.taskmanagement.service.UserService;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    IUserService userService;

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

        binding.btnSignUp.setOnClickListener(v -> {
            onSignUpClicked();
        });
        binding.tvAlreadyHaveAccount.setOnClickListener(v -> {
            onLoginClicked();
        });
    }

    private void onLoginClicked() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void onSignUpClicked() {
        String email = binding.etNewEmail.getText().toString();
        String password = binding.etNewPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();
        try {
            userService.register(email, password, confirmPassword);
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("email", email);
            loginIntent.putExtra("password", password);
            setResult(RESULT_OK, loginIntent);
            finish();

        }catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
}