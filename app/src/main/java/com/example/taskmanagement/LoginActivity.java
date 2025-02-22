package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    IUserService userService;

    private ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userService =  ((MyApp) getApplication()).getUserService();

        List<User> users = userService.getAllUsers();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnSignIn.setOnClickListener(v -> {
            onSignInClicked();
        });

        binding.tvCreateNewAccount.setOnClickListener(v -> { onCreateNewAccountClicked();});

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
        Intent intent = new Intent(this, RegisterActivity.class);
        activityResultLauncher.launch(intent);
    }

    private void onSignInClicked() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        User user = userService.login(email, password);
        if(user != null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("email", user.getEmail());
            startActivity(intent);
            finish();
        }

        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
    }
}