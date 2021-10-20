package it.bonny.app.wiseframe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import it.bonny.app.wiseframe.bean.SettingsBean;
import it.bonny.app.wiseframe.service.MyService;
import it.bonny.app.wiseframe.util.Utility;

public class LaunchOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utility utility = new Utility();
        SettingsBean settingsBean = utility.getLaunchOnBoot(context);
        Intent serviceIntent = new Intent(context, MyService.class);
        if(settingsBean.isLaunchBoot()) {
            if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}