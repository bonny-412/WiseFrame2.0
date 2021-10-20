package it.bonny.app.wiseframe.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.bean.ImageBean;
import it.bonny.app.wiseframe.bean.SettingsBean;
import it.bonny.app.wiseframe.db.ManagerDB;

public class Utility {
    public static  final String PREFS_NAME_FILE = "PrefsFileWiseFrame";
    private Toast toast = null;
    public Utility() {}

    public void exitAppByOnBackPressed(Context context){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(a);
    }

    public void showToast(String text, Context context) {
        if(context != null && text != null) {
            if(this.toast != null)
                this.toast.cancel();
            this.toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            this.toast.show();
        }
    }

    public List<ImageBean> getListImagesFromDB(Cursor cursor) {
        List<ImageBean> imageBeans = new ArrayList<>();
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    ImageBean imageBean = getImageBeanFromCursor(cursor);
                    if(imageBean != null)
                        imageBeans.add(imageBean);
                }while(cursor.moveToNext());
            }
        }
        return imageBeans;
    }

    private ImageBean getImageBeanFromCursor(Cursor cursor) {
        ImageBean imageBean = null;
        try {
            if(cursor != null) {
                imageBean = new ImageBean();
                imageBean.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_ID))));
                imageBean.setImagePath(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_PATH)));
                imageBean.setName(cursor.getString(cursor.getColumnIndex(ManagerDB.KEY_NAME)));
            }
        }catch (Exception e) {
            Log.e("WiseFrame", e.toString());
            return null;
        }
        return imageBean;
    }

    public void saveSettingsBean(SettingsBean settingsBean, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
        editor.putInt(SettingsNameValue.DISPLAY_TIME, settingsBean.getDisplayTime());
        editor.putInt(SettingsNameValue.DISPLAY_EFFECT, settingsBean.getDisplayEffect());
        editor.putInt(SettingsNameValue.TRANSITION_EFFECT, settingsBean.getTransitionEffect());
        editor.putInt(SettingsNameValue.PHOTO_ORDER, settingsBean.getPhotoOrder());
        editor.putBoolean(SettingsNameValue.DECORATION, settingsBean.isDecoration());
        editor.putBoolean(SettingsNameValue.KEEP_SCREEN_ON, settingsBean.isDisplayOn());
        editor.putString(SettingsNameValue.START_SCHEDULES, settingsBean.getStartSchedules());
        editor.putBoolean(SettingsNameValue.LAUNCH_BOOT, settingsBean.isLaunchBoot());
        editor.putBoolean(SettingsNameValue.ACTIVE_CRASH, settingsBean.isActiveCrash());
        editor.putBoolean(SettingsNameValue.ACTIVE_ANALYTICS, settingsBean.isActiveAnalytics());
        editor.putBoolean(SettingsNameValue.ACTIVE_MESSAGING, settingsBean.isActiveMessaging());
        editor.putStringSet(SettingsNameValue.LIST_DAYS, settingsBean.getListDays());
        editor.apply();
    }

    public SettingsBean getSettingBeanSaved(Activity activity) {
        SettingsBean settingsBean = new SettingsBean();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE);
        settingsBean.setDisplayTime(sharedPreferences.getInt(SettingsNameValue.DISPLAY_TIME, SettingsNameValue.SEC_10));
        settingsBean.setDisplayEffect(sharedPreferences.getInt(SettingsNameValue.DISPLAY_EFFECT, SettingsNameValue.DISPLAY_CENTER_INSIDE));
        settingsBean.setTransitionEffect(sharedPreferences.getInt(SettingsNameValue.TRANSITION_EFFECT, SettingsNameValue.TRANSITION_FADE));
        settingsBean.setPhotoOrder(sharedPreferences.getInt(SettingsNameValue.PHOTO_ORDER, SettingsNameValue.INSERT_DATE_RECENT));
        settingsBean.setDecoration(sharedPreferences.getBoolean(SettingsNameValue.DECORATION, false));
        settingsBean.setDisplayOn(sharedPreferences.getBoolean(SettingsNameValue.KEEP_SCREEN_ON, true));
        settingsBean.setStartSchedules(sharedPreferences.getString(SettingsNameValue.START_SCHEDULES, null));
        settingsBean.setLaunchBoot(sharedPreferences.getBoolean(SettingsNameValue.LAUNCH_BOOT, false));
        settingsBean.setActiveCrash(sharedPreferences.getBoolean(SettingsNameValue.ACTIVE_CRASH, true));
        settingsBean.setActiveAnalytics(sharedPreferences.getBoolean(SettingsNameValue.ACTIVE_ANALYTICS, true));
        settingsBean.setActiveMessaging(sharedPreferences.getBoolean(SettingsNameValue.ACTIVE_MESSAGING, true));
        settingsBean.setListDays(sharedPreferences.getStringSet(SettingsNameValue.LIST_DAYS, new HashSet<String>()));

        return settingsBean;
    }

    public void deleteSettingsBeanSaved(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(SettingsNameValue.DISPLAY_TIME);
        editor.remove(SettingsNameValue.DISPLAY_EFFECT);
        editor.remove(SettingsNameValue.TRANSITION_EFFECT);
        editor.remove(SettingsNameValue.PHOTO_ORDER);
        editor.remove(SettingsNameValue.DECORATION);
        editor.remove(SettingsNameValue.KEEP_SCREEN_ON);
        editor.remove(SettingsNameValue.START_SCHEDULES);
        editor.remove(SettingsNameValue.STOP_SCHEDULES);
        editor.remove(SettingsNameValue.LAUNCH_BOOT);
        editor.remove(SettingsNameValue.ACTIVE_CRASH);
        editor.remove(SettingsNameValue.ACTIVE_ANALYTICS);
        editor.remove(SettingsNameValue.ACTIVE_MESSAGING);
        editor.remove(SettingsNameValue.LIST_DAYS);
        editor.apply();
    }

    public void deleteAutomaticStart(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
        editor.remove(SettingsNameValue.LIST_DAYS);
        editor.remove(SettingsNameValue.START_SCHEDULES);
        editor.remove(SettingsNameValue.STOP_SCHEDULES);
        editor.apply();
    }

    public SettingsBean getLaunchOnBoot(Context context) {
        SettingsBean settingsBean = new SettingsBean();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME_FILE, Context.MODE_PRIVATE);
        settingsBean.setLaunchBoot(sharedPreferences.getBoolean(SettingsNameValue.LAUNCH_BOOT, false));
        return settingsBean;
    }

    public String getStringSelect(int value, Activity activity) {
        String result = "";
        switch (value) {
            case SettingsNameValue.SEC_10:
                result = 10 + " " + activity.getString(R.string.seconds);
                break;
            case SettingsNameValue.SEC_15:
                result = 15 + " " + activity.getString(R.string.seconds);
                break;
            case SettingsNameValue.SEC_30:
                result = 30 + " " + activity.getString(R.string.seconds);
                break;
            case SettingsNameValue.SEC_60:
                result = 1 + " " + activity.getString(R.string.minute);
                break;
            case SettingsNameValue.SEC_300:
                result = 5 + " " + activity.getString(R.string.minutes);
                break;
            case SettingsNameValue.INSERT_DATE_RECENT:
                result = activity.getString(R.string.insert_date_recent);
                break;
            case SettingsNameValue.INSERT_DATE_OLDER:
                result = activity.getString(R.string.insert_date_older);
                break;
            case SettingsNameValue.RANDOM:
                result = activity.getString(R.string.random);
                break;
            case SettingsNameValue.TRANSITION_FADE:
                result = activity.getString(R.string.transition_fade);
                break;
            case SettingsNameValue.TRANSITION_SLIDE:
                result = activity.getString(R.string.transition_slide);
                break;
            case SettingsNameValue.TRANSITION_ZOOM:
                result = activity.getString(R.string.transition_zoom);
                break;
            case SettingsNameValue.TRANSITION_EXPLOSION:
                result = activity.getString(R.string.transition_explosion);
                break;
            case SettingsNameValue.DISPLAY_CENTER_CROP:
                result = activity.getString(R.string.center_crop);
                break;
            case SettingsNameValue.DISPLAY_CENTER_INSIDE:
                result = activity.getString(R.string.center_inside);
                break;
            case SettingsNameValue.DISPLAY_FIT_CENTER:
                result = activity.getString(R.string.fit_center);
                break;
            case SettingsNameValue.DISPLAY_MATRIX:
                result = activity.getString(R.string.matrix);
                break;

        }
        return result;
    }

    public int findIdButtonBySettingBeanDisplayTime(SettingsBean settingsBean) {
        int id = 0;
        switch (settingsBean.getDisplayTime()) {
            case SettingsNameValue.SEC_10:
                id = R.id.sec10;
                break;
            case SettingsNameValue.SEC_15:
                id = R.id.sec15;
                break;
            case SettingsNameValue.SEC_30:
                id = R.id.sec30;
                break;
            case SettingsNameValue.SEC_60:
                id = R.id.sec60;
                break;
            case SettingsNameValue.SEC_300:
                id = R.id.sec300;
                break;
        }
        return id;
    }

    public int findIdButtonBySettingBeanPhotoOrder(SettingsBean settingsBean) {
        int id = 0;
        switch (settingsBean.getPhotoOrder()) {
            case SettingsNameValue.INSERT_DATE_RECENT:
                id = R.id.insertDateRecent;
                break;
            case SettingsNameValue.INSERT_DATE_OLDER:
                id = R.id.insertDateOlder;
                break;
            case SettingsNameValue.RANDOM:
                id = R.id.random;
                break;
        }
        return id;
    }

    public int findIdButtonBySettingBeanTransitionEffect(SettingsBean settingsBean) {
        int id = 0;
        switch (settingsBean.getTransitionEffect()) {
            case SettingsNameValue.TRANSITION_FADE:
                id = R.id.transitionFade;
                break;
            case SettingsNameValue.TRANSITION_SLIDE:
                id = R.id.transitionSlide;
                break;
            case SettingsNameValue.TRANSITION_ZOOM:
                id = R.id.transitionZoom;
                break;
            case SettingsNameValue.TRANSITION_EXPLOSION:
                id = R.id.transitionExplosion;
                break;
        }
        return id;
    }

    public int findIdButtonBySettingBeanDisplayEffect(SettingsBean settingsBean) {
        int id = 0;
        switch (settingsBean.getDisplayEffect()) {
            case SettingsNameValue.DISPLAY_CENTER_CROP:
                id = R.id.display_center_crop;
                break;
            case SettingsNameValue.DISPLAY_CENTER_INSIDE:
                id = R.id.display_center_inside;
                break;
            case SettingsNameValue.DISPLAY_FIT_CENTER:
                id = R.id.display_fit_center;
                break;
            case SettingsNameValue.DISPLAY_MATRIX:
                id = R.id.display_matrix;
                break;
        }
        return id;
    }

    public Button checkedButtonDisplayTimeUtil(int val, SettingsBean settingsBean, Button btn, Button btnChecked, Activity activity, View v) {
        settingsBean.setDisplayTime(val);
        btnChecked.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_check));
        btn.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_checked));
        int id = 0;
        switch (val) {
            case SettingsNameValue.SEC_10:
                id = R.id.sec10;
                break;
            case SettingsNameValue.SEC_15:
                id = R.id.sec15;
                break;
            case SettingsNameValue.SEC_30:
                id = R.id.sec30;
                break;
            case SettingsNameValue.SEC_60:
                id = R.id.sec60;
                break;
            case SettingsNameValue.SEC_300:
                id = R.id.sec300;
                break;
        }
        btnChecked = v.findViewById(id);
        return btnChecked;
    }

    public void checkedButtonDay(String val, SettingsBean settingsBean, Activity activity, Button button) {
        if(settingsBean.getListDays() == null)
            settingsBean.setListDays(new HashSet<String>());
        if (settingsBean.getListDays().contains(val)) {
            button.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_day_check));
            settingsBean.getListDays().remove(val);
        }else {
            button.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_day_checked));
            settingsBean.getListDays().add(val);
        }
    }

    public Button checkedButtonPhotoOrderUtil(int val, SettingsBean settingsBean, Button btn, Button btnChecked, Activity activity, View v) {
        settingsBean.setPhotoOrder(val);
        btnChecked.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_check));
        btn.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_checked));
        int id = 0;
        switch (val) {
            case SettingsNameValue.INSERT_DATE_RECENT:
                id = R.id.insertDateRecent;
                break;
            case SettingsNameValue.INSERT_DATE_OLDER:
                id = R.id.insertDateOlder;
                break;
            case SettingsNameValue.RANDOM:
                id = R.id.random;
                break;
        }
        btnChecked = v.findViewById(id);
        return btnChecked;
    }

    public Button checkedButtonTransitionEffectUtil(int val, SettingsBean settingsBean, Button btn, Button btnChecked, Activity activity, View v) {
        settingsBean.setTransitionEffect(val);
        if(btnChecked != null)
            btnChecked.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_check));
        btn.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_checked));
        int id = 0;
        switch (val) {
            case SettingsNameValue.TRANSITION_FADE:
                id = R.id.transitionFade;
                break;
            case SettingsNameValue.TRANSITION_SLIDE:
                id = R.id.transitionSlide;
                break;
            case SettingsNameValue.TRANSITION_ZOOM:
                id = R.id.transitionZoom;
                break;
            case SettingsNameValue.TRANSITION_EXPLOSION:
                id = R.id.transitionExplosion;
                break;
        }
        btnChecked = v.findViewById(id);
        return btnChecked;
    }

    public Button checkedButtonDisplayEffectUtil(int val, SettingsBean settingsBean, Button btn, Button btnChecked, Activity activity, View v) {
        settingsBean.setDisplayEffect(val);
        if(btnChecked != null)
            btnChecked.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_check));
        btn.setBackground(ContextCompat.getDrawable(activity, R.drawable.border_button_checked));
        int id = 0;
        switch (val) {
            case SettingsNameValue.DISPLAY_CENTER_CROP:
                id = R.id.display_center_crop;
                break;
            case SettingsNameValue.DISPLAY_CENTER_INSIDE:
                id = R.id.display_center_inside;
                break;
            case SettingsNameValue.DISPLAY_FIT_CENTER:
                id = R.id.display_fit_center;
                break;
            case SettingsNameValue.DISPLAY_MATRIX:
                id = R.id.display_matrix;
                break;
        }
        btnChecked = v.findViewById(id);
        return btnChecked;
    }
}

