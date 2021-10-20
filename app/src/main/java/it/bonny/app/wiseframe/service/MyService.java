package it.bonny.app.wiseframe.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.manager.SliderActivity;
import it.bonny.app.wiseframe.util.Utility;

public class MyService extends Service {
    private final Utility utility = new Utility();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Intent notificationIntent = new Intent(getApplicationContext() ,  SliderActivity.class) ;
        notificationIntent.putExtra( "fromNotification" , true ) ;
        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0 , notificationIntent , 0 ) ;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , getString(R.string.notification_channel_id)) ;
        mBuilder.setContentTitle( "My Notification" ) ;
        mBuilder.setContentIntent(pendingIntent) ;
        mBuilder.setContentText( "Notification Listener Service Example" ) ;
        mBuilder.setSmallIcon(R.drawable.ic_wiseframe_notify) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager. IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new  NotificationChannel(getString(R.string.notification_channel_id) , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId(getString(R.string.notification_channel_id) ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
        throw new UnsupportedOperationException( "Not yet implemented" ) ;
    }

    public void onDestroy() {
        utility.showToast("MyService stopped", this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent i = new Intent(getBaseContext(), SliderActivity.class);
        i.putExtra("receiver", 1);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        return super.onStartCommand(i, flags, startId);
    }
}
