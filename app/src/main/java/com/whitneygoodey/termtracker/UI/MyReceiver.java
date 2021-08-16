package com.whitneygoodey.termtracker.UI;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.whitneygoodey.termtracker.R;

public class MyReceiver extends BroadcastReceiver {

    String channel_id = "test";
    static int notificationID;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, intent.getStringExtra("content"), Toast.LENGTH_LONG).show();
        String title = intent.getStringExtra("type");
        createNotificationChannel(context, channel_id);
        Notification n = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(intent.getStringExtra("content"))
                .setContentTitle(title)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, n);
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel(Context context, String CHANNEL_ID) {
        CharSequence name = context.getResources().getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        //Register the channel with the system; you can't change the importance
        //or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}