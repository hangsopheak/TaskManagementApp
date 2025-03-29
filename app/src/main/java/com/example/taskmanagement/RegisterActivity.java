package com.example.taskmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityRegisterBinding;
import com.example.taskmanagement.service.IUserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

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

        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Confirm password doesn't match!", Toast.LENGTH_SHORT).show();
        }else {
            binding.pbRegisterProgress.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                binding.pbRegisterProgress.setVisibility(View.INVISIBLE);
                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                loginIntent.putExtra("email", email);
                                loginIntent.putExtra("password", password);
                                setResult(RESULT_OK, loginIntent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                binding.pbRegisterProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }
    }

    private void onAlreadyHaveAccountClicked() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}