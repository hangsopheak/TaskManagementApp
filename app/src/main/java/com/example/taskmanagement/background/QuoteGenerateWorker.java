package com.example.taskmanagement.background;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Random;

public class QuoteGenerateWorker extends Worker {
    public QuoteGenerateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    private static final String[] QUOTES = {
            "Stay hungry, stay foolish.",
            "Life is short. Code more.",
            "Talk is cheap. Show me the code.",
            "Dream big. Work hard.",
            "First, solve the problem. Then, write the code.",
            "Simplicity is the soul of efficiency.",
            "Code is like humor. When you have to explain it, it’s bad.",
            "Experience is the name everyone gives to their mistakes.",
            "Make it work, make it right, make it fast.",
            "Programs must be written for people to read.",
            "Fix the cause, not the symptom.",
            "If it hurts, do it more often.",
            "You can’t manage what you can’t measure.",
            "Any fool can write code that a computer can understand. Good programmers write code that humans can understand.",
            "Before software can be reusable it first has to be usable.",
            "The best error message is the one that never shows up."
    };


    @NonNull
    @Override
    public Result doWork() {
        try {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "📜 Quote: " + getRandomQuote(), Toast.LENGTH_LONG).show());
            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    public static String getRandomQuote() {
        int index = new Random().nextInt(QUOTES.length);
        return QUOTES[index];
    }
}
