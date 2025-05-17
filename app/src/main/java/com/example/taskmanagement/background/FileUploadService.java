package com.example.taskmanagement.background;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FileUploadService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Handler().postDelayed(() -> {
            Toast.makeText(getApplicationContext(), "✅ File upload complete", Toast.LENGTH_SHORT).show();
            stopSelf();
        }, 10000);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
