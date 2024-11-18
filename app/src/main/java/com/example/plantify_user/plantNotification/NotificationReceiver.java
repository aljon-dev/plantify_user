package com.example.plantify_user.plantNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;

import com.example.plantify_user.MainActivity;
import com.example.plantify_user.R;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "PlantScheduleChannel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String plantName = intent.getStringExtra("plantName");

        Intent notificationIntent = new Intent(context, MainActivity.class); // Change to your main activity
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Plant Schedule Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.plant_logo) // Replace with your icon
                .setContentTitle("Plant Reminder")
                .setContentText("Time to take care of " + plantName + "!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }
}