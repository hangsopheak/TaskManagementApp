package com.example.taskmanagement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.taskmanagement.databinding.ActivityNotificationTestBinding;

public class NotificationTestActivity extends AppCompatActivity {

    ActivityNotificationTestBinding binding;
    private NotificationManager notificationManager;
    private final String CHANNEL_ID = "task_notify_channel";
    private static final int REQ_NOTIF = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationTestBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        // ensure we have runtime notification permission on Android 13+
        ensureNotificationPermission();

        binding.btnBasic.setOnClickListener(v -> showBasicNotification());
        binding.btnBigText.setOnClickListener(v -> showBigTextNotification());
        binding.btnBigImage.setOnClickListener(v -> showBigPictureNotification());
        binding.btnAction.setOnClickListener(v -> showActionNotification());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Notification";
            String description = "Channel for Task Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Request POST_NOTIFICATIONS at runtime for Android 13+
    private void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIF);
            }
        }
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void safeNotify(NotificationManager nm, int id, android.app.Notification notification) {
        if (!hasNotificationPermission()) return;
        nm.notify(id, notification);
    }

    private void showBasicNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Basic Notification")
                .setContentText("This is a basic notification example.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        safeNotify(notificationManager, 1, builder.build());
    }

    private void showBigTextNotification() {
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Big Text Notification")
                .bigText("This is an expanded notification example showing a longer piece of text that wouldn’t fit in a standard notification view.");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(bigText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        safeNotify(notificationManager, 2, builder.build());
    }

    private void showBigPictureNotification() {
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.teamwork);
        Bitmap bigPictureBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.promotion);


        NotificationCompat.BigPictureStyle bigPicture = new NotificationCompat.BigPictureStyle()
                .bigPicture(bigPictureBitmap)
                .bigLargeIcon(largeIconBitmap);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Big Picture Notification")
                .setContentText("This notification contains a big image.")
                .setStyle(bigPicture)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        safeNotify(notificationManager, 3, builder.build());
    }

    private void showActionNotification() {
        Intent intent = new Intent(this, NotificationTestActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Action Notification")
                .setContentText("Tap to open the app.")
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Open", pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        safeNotify(notificationManager, 4, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIF) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                Toast.makeText(this, "Notification permission denied. Notifications will be disabled.", Toast.LENGTH_LONG).show();
            }
        }
    }
}