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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskmanagement.databinding.ActivityNotificationTestBinding;

public class NotificationTestActivity extends AppCompatActivity {

    ActivityNotificationTestBinding binding;
    private NotificationManager notificationManager;
    private final String CHANNEL_ID = "task_notify_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationTestBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

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

    private void showBasicNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Basic Notification")
                .setContentText("This is a basic notification example.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    private void showBigTextNotification() {
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Big Text Notification")
                .bigText("This is an expanded notification example showing a longer piece of text that wouldn’t fit in a standard notification view.");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(bigText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(2, builder.build());
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

        notificationManager.notify(3, builder.build());
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

        notificationManager.notify(4, builder.build());
    }
}