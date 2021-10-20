package it.bonny.app.wiseframe.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.nio.charset.StandardCharsets;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.bean.SettingsBean;
import it.bonny.app.wiseframe.util.SettingsNameValue;
import it.bonny.app.wiseframe.util.Utility;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private final Utility utility = new Utility();
    private SettingsBean settingsBean = null;
    private final Context context = this;
    private final Activity activity = this;
    private RelativeLayout displayTimeSettings, displayEffect, transitionEffect, photoOrder,
            ornament, contactDeveloper, commentApp, clearCacheImage, otherApp;
    private SwitchCompat switchAlwaysOnScreen, switchLaunchBoot, switchOrnament, switchBugCrash, switchReports;
    private TextView selectDisplayTime, selectAlwaysOnScreen, selectLaunchBoot, selectPhotoOrder, selectTransitionEffect,
            selectOrnament, selectDisplayEffect;
    private Button buttonCheckedDisplayTime = null;
    private Animation slideDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsBean = utility.getSettingBeanSaved(activity);
        if(settingsBean != null){
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingsBean.isActiveAnalytics());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, savedInstanceState);
        }
        setContentView(R.layout.settings_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.toolbar_action_bar));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //utility.deleteSettingsBeanSaved(activity);
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        initElement();

        displayTimeSettings.setOnClickListener(this);
        displayEffect.setOnClickListener(this);
        transitionEffect.setOnClickListener(this);
        photoOrder.setOnClickListener(this);
        ornament.setOnClickListener(this);
        contactDeveloper.setOnClickListener(this);
        commentApp.setOnClickListener(this);
        photoOrder.setOnClickListener(this);
        clearCacheImage.setOnClickListener(this);
        otherApp.setOnClickListener(this);

        switchAlwaysOnScreen.setOnCheckedChangeListener(this);
        switchLaunchBoot.setOnCheckedChangeListener(this);
        switchOrnament.setOnCheckedChangeListener(this);
        switchBugCrash.setOnCheckedChangeListener(this);
        switchReports.setOnCheckedChangeListener(this);
    }

    private void initElement() {
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);

        displayTimeSettings = findViewById(R.id.display_time_settings);
        displayEffect = findViewById(R.id.display_effect);
        transitionEffect = findViewById(R.id.transition_effect);
        photoOrder = findViewById(R.id.photo_order);
        ornament = findViewById(R.id.ornament);
        contactDeveloper = findViewById(R.id.contact_developer);
        commentApp = findViewById(R.id.comment_app);
        clearCacheImage = findViewById(R.id.clear_cache_image);
        otherApp = findViewById(R.id.other_app);

        switchAlwaysOnScreen = findViewById(R.id.switch_always_on_screen);
        switchLaunchBoot = findViewById(R.id.switch_launch_boot);
        switchOrnament = findViewById(R.id.switch_ornament);
        switchBugCrash = findViewById(R.id.switch_bug_crash);
        switchReports = findViewById(R.id.switch_reports);

        selectDisplayTime = findViewById(R.id.select_display_time);
        selectPhotoOrder = findViewById(R.id.select_photo_order);
        selectAlwaysOnScreen = findViewById(R.id.select_always_on_screen);
        selectLaunchBoot = findViewById(R.id.select_launch_boot);
        selectTransitionEffect = findViewById(R.id.select_transition_effect);
        selectOrnament = findViewById(R.id.select_ornament);
        selectDisplayEffect = findViewById(R.id.select_display_effect);

        //Select string
        selectDisplayTime.setText(utility.getStringSelect(settingsBean.getDisplayTime(), activity));
        selectPhotoOrder.setText(utility.getStringSelect(settingsBean.getPhotoOrder(), activity));
        selectAlwaysOnScreen.setText(settingsBean.isDisplayOn() ? getString(R.string.activated) : getString(R.string.disabled));
        selectLaunchBoot.setText(settingsBean.isLaunchBoot() ? getString(R.string.activated) : getString(R.string.disabled));
        selectOrnament.setText(settingsBean.isDecoration() ? getString(R.string.activated) : getString(R.string.disabled));
        selectTransitionEffect.setText(utility.getStringSelect(settingsBean.getTransitionEffect(), activity));
        selectDisplayEffect.setText(utility.getStringSelect(settingsBean.getDisplayEffect(), activity));

        //Value switch
        switchAlwaysOnScreen.setChecked(settingsBean.isDisplayOn());
        switchLaunchBoot.setChecked(settingsBean.isLaunchBoot());
        switchOrnament.setChecked(settingsBean.isDecoration());
        switchBugCrash.setChecked(settingsBean.isActiveCrash());
        switchReports.setChecked(settingsBean.isActiveAnalytics());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.display_time_settings:
                createDialogShowDisplayTime();
                break;
            case R.id.display_effect:
                createDialogDisplayEffect();
                break;
            case R.id.transition_effect:
                createDialogTransitionEffect();
                break;
            case R.id.photo_order:
                createDialogPhotoOrder();
                break;
            case R.id.ornament:
                utility.showToast("ornament", this);
                break;
            case R.id.comment_app:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                break;
            case R.id.contact_developer:
                byte[] data = Base64.decode(SettingsNameValue.EMAIL, Base64.DEFAULT);
                String[] TO = {new String(data, StandardCharsets.UTF_8)};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));

                startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.send_email_title)));
                break;
            case R.id.clear_cache_image:
                Glide.get(context).clearMemory();
                utility.showToast(getString(R.string.clear_cache_successfully), context);
                break;
            case R.id.other_app:
                final String url = "https://play.google.com/store/apps/dev?id=6114894737573241391";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_always_on_screen:
                settingsBean.setDisplayOn(isChecked);
                selectAlwaysOnScreen.setText(isChecked ? getString(R.string.activated) : getString(R.string.disabled));
                utility.saveSettingsBean(settingsBean, activity);
                break;
            case R.id.switch_launch_boot:
                settingsBean.setLaunchBoot(isChecked);
                selectLaunchBoot.setText(isChecked ? getString(R.string.activated) : getString(R.string.disabled));
                utility.saveSettingsBean(settingsBean, activity);
                break;
            case R.id.switch_ornament:
                settingsBean.setDecoration(isChecked);
                selectOrnament.setText(isChecked ? getString(R.string.activated) : getString(R.string.disabled));
                utility.saveSettingsBean(settingsBean, activity);
                break;
            case R.id.switch_bug_crash:
                settingsBean.setActiveCrash(isChecked);
                utility.saveSettingsBean(settingsBean, activity);
                break;
            case R.id.switch_reports:
                settingsBean.setActiveAnalytics(isChecked);
                utility.saveSettingsBean(settingsBean, activity);
                break;
        }
    }

    //Shows the display time alert
    private void createDialogShowDisplayTime(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View viewSaveDialog = View.inflate(context, R.layout.dialog_display_time, null);
        builder.setCancelable(false);
        builder.setView(viewSaveDialog);
        final Button buttonClose = viewSaveDialog.findViewById(R.id.btn_delete);
        buttonClose.startAnimation(slideDown);
        final int idChecked = utility.findIdButtonBySettingBeanDisplayTime(settingsBean);
        buttonCheckedDisplayTime = viewSaveDialog.findViewById(idChecked);
        buttonCheckedDisplayTime.setBackground(ContextCompat.getDrawable(context, R.drawable.border_button_checked));
        final AlertDialog dialog = builder.create();
        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDisplayTime.setText(utility.getStringSelect(settingsBean.getDisplayTime(), activity));
                utility.saveSettingsBean(settingsBean, activity);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void checkedButton(View v) {
        Button button = (Button) v;
        final Animation scale = AnimationUtils.loadAnimation(context, R.anim.scale);
        button.startAnimation(scale);
        switch (v.getId()) {
            case R.id.sec10:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayTimeUtil(SettingsNameValue.SEC_10, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.sec15:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayTimeUtil(SettingsNameValue.SEC_15, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.sec30:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayTimeUtil(SettingsNameValue.SEC_30, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.sec60:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayTimeUtil(SettingsNameValue.SEC_60, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.sec300:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayTimeUtil(SettingsNameValue.SEC_300, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.insertDateRecent:
                buttonCheckedDisplayTime = utility.checkedButtonPhotoOrderUtil(SettingsNameValue.INSERT_DATE_RECENT, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.insertDateOlder:
                buttonCheckedDisplayTime = utility.checkedButtonPhotoOrderUtil(SettingsNameValue.INSERT_DATE_OLDER, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.random:
                buttonCheckedDisplayTime = utility.checkedButtonPhotoOrderUtil(SettingsNameValue.RANDOM, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.transitionFade:
                buttonCheckedDisplayTime = utility.checkedButtonTransitionEffectUtil(SettingsNameValue.TRANSITION_FADE, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.transitionSlide:
                buttonCheckedDisplayTime = utility.checkedButtonTransitionEffectUtil(SettingsNameValue.TRANSITION_SLIDE, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.transitionZoom:
                buttonCheckedDisplayTime = utility.checkedButtonTransitionEffectUtil(SettingsNameValue.TRANSITION_ZOOM, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.transitionExplosion:
                buttonCheckedDisplayTime = utility.checkedButtonTransitionEffectUtil(SettingsNameValue.TRANSITION_EXPLOSION, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.display_center_crop:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayEffectUtil(SettingsNameValue.DISPLAY_CENTER_CROP, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.display_center_inside:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayEffectUtil(SettingsNameValue.DISPLAY_CENTER_INSIDE, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.display_fit_center:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayEffectUtil(SettingsNameValue.DISPLAY_FIT_CENTER, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
            case R.id.display_matrix:
                buttonCheckedDisplayTime = utility.checkedButtonDisplayEffectUtil(SettingsNameValue.DISPLAY_MATRIX, settingsBean, button, buttonCheckedDisplayTime, activity, v);
                break;
        }
    }

    private void createDialogPhotoOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View viewSaveDialog = View.inflate(context, R.layout.dialog_photo_order, null);
        builder.setCancelable(false);
        builder.setView(viewSaveDialog);
        final Button buttonClose = viewSaveDialog.findViewById(R.id.btn_delete);
        buttonClose.startAnimation(slideDown);
        final int idChecked = utility.findIdButtonBySettingBeanPhotoOrder(settingsBean);
        buttonCheckedDisplayTime = viewSaveDialog.findViewById(idChecked);
        buttonCheckedDisplayTime.setBackground(ContextCompat.getDrawable(context, R.drawable.border_button_checked));
        final AlertDialog dialog = builder.create();
        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoOrder.setText(utility.getStringSelect(settingsBean.getPhotoOrder(), activity));
                utility.saveSettingsBean(settingsBean, activity);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createDialogTransitionEffect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View viewSaveDialog = View.inflate(context, R.layout.dialog_transition_effect, null);
        builder.setCancelable(false);
        builder.setView(viewSaveDialog);
        final Button buttonClose = viewSaveDialog.findViewById(R.id.btn_delete);
        buttonClose.startAnimation(slideDown);
        final int idChecked = utility.findIdButtonBySettingBeanTransitionEffect(settingsBean);
        buttonCheckedDisplayTime = viewSaveDialog.findViewById(idChecked);
        if(buttonCheckedDisplayTime != null)
            buttonCheckedDisplayTime.setBackground(ContextCompat.getDrawable(context, R.drawable.border_button_checked));
        final AlertDialog dialog = builder.create();
        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTransitionEffect.setText(utility.getStringSelect(settingsBean.getTransitionEffect(), activity));
                utility.saveSettingsBean(settingsBean, activity);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createDialogDisplayEffect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View viewSaveDialog = View.inflate(context, R.layout.dialog_display_effect, null);
        builder.setCancelable(false);
        builder.setView(viewSaveDialog);
        final Button buttonClose = viewSaveDialog.findViewById(R.id.btn_delete);
        buttonClose.startAnimation(slideDown);
        final int idChecked = utility.findIdButtonBySettingBeanDisplayEffect(settingsBean);
        buttonCheckedDisplayTime = viewSaveDialog.findViewById(idChecked);
        if(buttonCheckedDisplayTime != null)
            buttonCheckedDisplayTime.setBackground(ContextCompat.getDrawable(context, R.drawable.border_button_checked));
        final AlertDialog dialog = builder.create();
        if(dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDisplayEffect.setText(utility.getStringSelect(settingsBean.getDisplayEffect(), activity));
                utility.saveSettingsBean(settingsBean, activity);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
