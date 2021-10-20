package it.bonny.app.wiseframe.manager;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.animation.ExplosionField;
import it.bonny.app.wiseframe.bean.ImageBean;
import it.bonny.app.wiseframe.bean.SettingsBean;
import it.bonny.app.wiseframe.db.ManagerDB;
import it.bonny.app.wiseframe.util.SettingsNameValue;
import it.bonny.app.wiseframe.util.Utility;

public class SliderActivity extends AppCompatActivity {
    private final Activity activity = this;
    private final Context context = this;
    private final ManagerDB managerDB = new ManagerDB(this);
    private final Utility utility = new Utility();
    private final Handler handler = new Handler();
    private final Handler handlerDecoration = new Handler();
    private List<ImageBean> imageBeans;
    private Runnable runnable = null;
    private Runnable runnableDecoration = null;
    private int count = 0;
    private ImageView imgSlider, imgSlider1;
    private boolean showImg1 = true;
    private Animation animationIn = null, animationOut = null;
    private SettingsBean settingsBean = null;
    private ExplosionField explodeAnimation = null;
    private boolean sliderOn = false;
    private NotificationManagerCompat notificationManagerCompat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsBean = utility.getSettingBeanSaved(activity);
        if(settingsBean != null){
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingsBean.isActiveAnalytics());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, savedInstanceState);
        }
        settingsDisplay();
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        setContentView(R.layout.activity_slider);
        managerDB.openReadDB();
        imageBeans = utility.getListImagesFromDB(managerDB.getAllImages(settingsBean.getPhotoOrder()));
        managerDB.close();
        if(imageBeans == null || imageBeans.size() == 0) {
            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
        }else {
            if (settingsBean.isDisplayOn())
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if(settingsBean.isDecoration()) {
                RelativeLayout relativeLayout = activity.findViewById(R.id.rel_decoration);
                relativeLayout.setVisibility(View.VISIBLE);
                setDayToDecoration();
                setHourToDecoration();
            }
            imgSlider = findViewById(R.id.imgSlider);
            imgSlider1 = findViewById(R.id.imgSlider1);
            notificationManagerCompat = NotificationManagerCompat.from(this);
            setScaleTypeImg();
            sliderOn = true;
            Glide.with(activity).load(imageBeans.get(count).getImagePath()).into(imgSlider);
            showBlurryImage(BitmapFactory.decodeFile(imageBeans.get(count).getImagePath()));
            if(settingsBean.getPhotoOrder() == SettingsNameValue.RANDOM)
                count = randomPhoto(imageBeans.size());
            else
                count ++;
            changePageSlider();
            if(settingsBean.isDecoration())
                showDecoration();
        }
    }

    private void changePageSlider() {
        try {
            switch (settingsBean.getTransitionEffect()) {
                case SettingsNameValue.TRANSITION_FADE:
                    animationIn = AnimationUtils.loadAnimation(context, R.anim.slider_fade_in);
                    animationOut = AnimationUtils.loadAnimation(context, R.anim.slider_fade_out);
                    explodeAnimation = null;
                    break;
                case SettingsNameValue.TRANSITION_SLIDE:
                    animationIn = AnimationUtils.loadAnimation(context, R.anim.slider_slide_up);
                    animationOut = AnimationUtils.loadAnimation(context, R.anim.slider_slide_down);
                    explodeAnimation = null;
                    break;
                case SettingsNameValue.TRANSITION_ZOOM:
                    animationIn = AnimationUtils.loadAnimation(context, R.anim.slider_zoom_in);
                    animationOut = AnimationUtils.loadAnimation(context, R.anim.slider_zoom_out);
                    explodeAnimation = null;
                    break;
                case SettingsNameValue.TRANSITION_EXPLOSION:
                    animationIn = null;
                    animationOut = null;
                    explodeAnimation = ExplosionField.attach2Window(activity);
                    break;
            }
            runnable = new Runnable() {
                @Override
                public void run() {
                    if(settingsBean.isDecoration())
                        setDayToDecoration();

                    if(settingsBean.getPhotoOrder() == SettingsNameValue.RANDOM)
                        count = randomPhoto(imageBeans.size());
                    else {
                        if(count == imageBeans.size())
                            count = 0;
                    }
                    if(showImg1) {
                        Glide.with(activity).load(imageBeans.get(count).getImagePath()).into(imgSlider1);
                        if((animationIn != null && animationOut != null) && explodeAnimation == null) {
                            imgSlider.startAnimation(animationOut);
                            imgSlider1.startAnimation(animationIn);
                        }else if((animationIn == null && animationOut == null) && explodeAnimation != null) {
                            explodeAnimation.resetViewAfterExplosion(imgSlider1);
                            explodeAnimation.explode(imgSlider);
                        }
                        showImg1 = false;
                    }else {
                        Glide.with(activity).load(imageBeans.get(count).getImagePath()).into(imgSlider);
                        if((animationIn != null && animationOut != null) && explodeAnimation == null) {
                            imgSlider1.startAnimation(animationOut);
                            imgSlider.startAnimation(animationIn);
                        }else if((animationIn == null && animationOut == null) && explodeAnimation != null) {
                            explodeAnimation.resetViewAfterExplosion(imgSlider);
                            explodeAnimation.explode(imgSlider1);
                        }
                        showImg1 = true;
                    }
                    showBlurryImage(BitmapFactory.decodeFile(imageBeans.get(count).getImagePath()));
                    if(settingsBean.getPhotoOrder() != SettingsNameValue.RANDOM)
                        count ++;
                    handler.postDelayed(runnable, settingsBean.getDisplayTimeInMilliSeconds());
                }
            };
            handler.postDelayed(runnable, settingsBean.getDisplayTimeInMilliSeconds());
        }catch (Exception e) {
            activity.recreate();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

    }

    private void showDecoration() {
        if(settingsBean.isDecoration()) {
            runnableDecoration = new Runnable() {
                @Override
                public void run() {
                    setHourToDecoration();
                    handlerDecoration.postDelayed(runnableDecoration, 1000);
                }
            };
            handlerDecoration.postDelayed(runnableDecoration, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
        super.onBackPressed();
        handler.removeCallbacks(runnable);
        sliderOn = false;
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(sliderOn) {
            Intent activityIntent = new Intent(this, SliderActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notification = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
                    .setSmallIcon(R.drawable.ic_wiseframe_notify)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_wiseframe_notify))
                    .setContentTitle(getString(R.string.presentation_on_1))
                    .setContentText(getString(R.string.presentation_on_2))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setDefaults(0)
                    .setVibrate(new long[0])
                    .setOnlyAlertOnce(true)
                    .build();
            notificationManagerCompat.notify(0, notification);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        settingsDisplay();
        if(notificationManagerCompat != null)
            notificationManagerCompat.cancel(0);
    }

    private int randomPhoto(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }

    private void setScaleTypeImg() {
        if(settingsBean.getDisplayEffect() == SettingsNameValue.DISPLAY_CENTER_CROP) {
            imgSlider.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgSlider1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else if(settingsBean.getDisplayEffect() == SettingsNameValue.DISPLAY_CENTER_INSIDE) {
            imgSlider.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imgSlider1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }else if(settingsBean.getDisplayEffect() == SettingsNameValue.DISPLAY_FIT_CENTER) {
            imgSlider.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgSlider1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else if(settingsBean.getDisplayEffect() == SettingsNameValue.DISPLAY_MATRIX) {
            imgSlider.setScaleType(ImageView.ScaleType.MATRIX);
            imgSlider1.setScaleType(ImageView.ScaleType.MATRIX);
        }
    }

    private void setDayToDecoration() {
        TextView day = findViewById(R.id.day);
        SimpleDateFormat eee_d_mmm = new SimpleDateFormat("EEE d MMM", Locale.getDefault());
        day.setText(eee_d_mmm.format(new Date()));
    }

    private void setHourToDecoration() {
        TextView schedule = findViewById(R.id.schedule);
        SimpleDateFormat hh_mm = new SimpleDateFormat("HH : mm", Locale.getDefault());
        schedule.setText(hh_mm.format(new Date()));
    }

    private void showBlurryImage(Bitmap bitmap) {
        final float BITMAP_SCALE = 0.5f;
        final float BLUR_RADIUS = 25f;
        try {
            if(bitmap != null) {
                ImageView imageView = findViewById(R.id.blurryImage);

                int width = Math.round(bitmap.getWidth() * BITMAP_SCALE);
                int height = Math.round(bitmap.getHeight() * BITMAP_SCALE);

                Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

                RenderScript renderScript = RenderScript.create(context);
                ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
                Allocation tmpIn = Allocation.createFromBitmap(renderScript, inputBitmap);
                Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
                scriptIntrinsicBlur.setRadius(BLUR_RADIUS);
                scriptIntrinsicBlur.setInput(tmpIn);
                scriptIntrinsicBlur.forEach(tmpOut);
                tmpOut.copyTo(outputBitmap);

                Glide.with(activity).load(outputBitmap).placeholder(ContextCompat.getDrawable(context, R.color.widget_transparent_color)).into(imageView);
            }
        }catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void settingsDisplay() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

}
