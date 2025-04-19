package com.example.taskmanagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.taskmanagement.databinding.ActivityMainBinding;
import com.example.taskmanagement.fragment.CalendarFragment;
import com.example.taskmanagement.fragment.CategoriesFragment;
import com.example.taskmanagement.fragment.SettingFragment;
import com.example.taskmanagement.fragment.TasksFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_MIC_PERMISSION = 1;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;


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

        setupSpeechRecognizer();

        binding.btnRecordTask.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    checkAndRequestMicPermission();
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    binding.micLottie.cancelAnimation();
                    binding.micLottie.setVisibility(View.GONE);
                    stopVoiceRecognition();
                    return true;
            }
            return false;
        });
    }

    private void startVoiceRecognition() {
        speechRecognizer.startListening(recognizerIntent);
    }

    private void stopVoiceRecognition() {
        speechRecognizer.stopListening();
    }

    private void checkAndRequestMicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, safe to proceed
            binding.micLottie.setVisibility(View.VISIBLE);
            binding.micLottie.playAnimation();
            startVoiceRecognition();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            // User denied before
            new AlertDialog.Builder(this)
                    .setTitle("Microphone Permission Needed")
                    .setMessage("This feature requires microphone access to record new task from voice input.")
                    .setPositiveButton("Grant Permission", (dialog, which) -> {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                REQUEST_CODE_MIC_PERMISSION);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
        else {
            // First time request
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_MIC_PERMISSION);
        }
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your task");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0);
                    handleSpokenTaskText(spokenText);
                }
            }

            // Optional: handle feedback
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_MIC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted
                startVoiceRecognition();
            }
            else {
                // Denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    // User clicked "Don't ask again"
                    new AlertDialog.Builder(this)
                            .setTitle("Microphone Permission Blocked")
                            .setMessage("You've permanently denied the microphone permission.\nPlease enable it manually in App Settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings())
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    Toast.makeText(this, "Permission denied, cannot start voice input.", Toast.LENGTH_SHORT).show();
                }
            }
            }

    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    private void handleSpokenTaskText(String spokenText) {
        Intent intent = new Intent(this, NewTaskActivity.class);
        intent.putExtra("title", spokenText.trim());
        startActivity(intent);
    }

    private void LoadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}