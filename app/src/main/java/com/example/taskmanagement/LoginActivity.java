package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityLoginBinding;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.IUserService;
import com.example.taskmanagement.service.MyApp;
import com.example.taskmanagement.service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    IUserService userService;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

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
        binding.pbLoginProgress.setVisibility(View.VISIBLE);
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.pbLoginProgress.setVisibility(View.INVISIBLE);
                            reload();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            binding.pbLoginProgress.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
}