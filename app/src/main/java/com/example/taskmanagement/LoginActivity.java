package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityLoginBinding;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.IUserService;
import com.example.taskmanagement.service.MyApp;
import com.example.taskmanagement.service.UserService;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    IUserService userService;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userService =  ((MyApp) getApplication()).getUserService();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSignIn.setOnClickListener(v -> {
            onSignInClicked();
        });

        binding.tvCreateNewAccount.setOnClickListener(v -> {
            onCreateNewAccountClicked();
        });

        userService.getAllUsers().forEach(u ->{
            Log.d("LoginActivity", u.getEmail() + " " + u.getPassword());
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        String email = data.getStringExtra("email");
                        String password = data.getStringExtra("password");

                        binding.etEmail.setText(email);
                        binding.etPassword.setText(password);
                    }
                }
        );


    }

    private void onCreateNewAccountClicked() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void onSignInClicked() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        User user = userService.login(email, password);
        if(user == null){
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }
    }
}