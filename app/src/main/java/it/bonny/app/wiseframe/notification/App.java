package it.bonny.app.wiseframe.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import it.bonny.app.wiseframe.R;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(getString(R.string.notification_channel_id), "Channel 1", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("This is Channel 1");
            channel1.enableVibration(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager != null)
                manager.createNotificationChannel(channel1);
        }
    }
}
