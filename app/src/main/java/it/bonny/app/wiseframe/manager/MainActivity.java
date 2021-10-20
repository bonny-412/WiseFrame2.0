package it.bonny.app.wiseframe.manager;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.bean.ImageBean;
import it.bonny.app.wiseframe.bean.SettingsBean;
import it.bonny.app.wiseframe.db.ManagerDB;
import it.bonny.app.wiseframe.util.Utility;

public class MainActivity extends AppCompatActivity {
    private final Context context = this;
    private final Activity activity = this;
    private CardView icGallery = null, icPhotoSelected = null, icGooglePhoto = null, icDropbox = null, icOther = null, icSettings = null;
    private CardView startPresentation = null;
    private ImageButton infoApp = null;
    private long backPressedTime;
    private final Utility utility = new Utility();
    private static final int PICK_IMAGE = 1;
    private ManagerDB managerDB = null;
    private Animation scale;
    private boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        SettingsBean settingsBean = utility.getSettingBeanSaved(activity);
        if(settingsBean != null) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(settingsBean.isActiveCrash());
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingsBean.isActiveAnalytics());
            FirebaseMessaging.getInstance().setAutoInitEnabled(settingsBean.isActiveAnalytics());
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, savedInstanceState);
        setContentView(R.layout.activity_main);
        initElement();
        showWelcomeAlert();
        if(!firstStart)
            showUsageInfoAlert();
        this.overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        icGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                    try {
                        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(gallery, PICK_IMAGE);
                    }catch (Exception e) {
                        utility.showToast(getString(R.string.error_app), context);
                    }
                }
            }
        });

        icGooglePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                    try {
                        getPackageManager().getPackageInfo("com.google.android.apps.photos", PackageManager.GET_META_DATA);
                        Intent googlePhotosIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        googlePhotosIntent.setPackage("com.google.android.apps.photos");
                        googlePhotosIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        googlePhotosIntent.setType("image/*");
                        startActivityForResult(googlePhotosIntent, PICK_IMAGE);
                    }catch (PackageManager.NameNotFoundException e) {
                        utility.showToast(getString(R.string.error_google_photos), context);
                    }
                }
            }
        });

        icDropbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                    try {
                        getPackageManager().getPackageInfo("com.dropbox.android", PackageManager.GET_META_DATA);
                        Intent dropboxIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        dropboxIntent.setPackage("com.dropbox.android");
                        dropboxIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        dropboxIntent.setType("image/*");
                        startActivityForResult(dropboxIntent, PICK_IMAGE);
                    }catch (PackageManager.NameNotFoundException e) {
                        utility.showToast(getString(R.string.error_dropbox), context);
                    }
                }
            }
        });

        icOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                    try {
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");
                        getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {});
                        startActivityForResult(chooserIntent, PICK_IMAGE);
                    }catch (Exception e) {
                        utility.showToast(getString(R.string.error_app), context);
                    }
                }
            }
        });

        icPhotoSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
                Intent intent = new Intent(context, GalleryActivity.class);
                startActivity(intent);
            }
        });

        icSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
            }
        });

        startPresentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
                Intent intent = new Intent(context, SliderActivity.class);
                startActivity(intent);
            }
        });

        startPresentation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                utility.showToast(getString(R.string.button_start_presentation), context);
                return true;
            }
        });

        infoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoApp.startAnimation(scale);
                showDialogInfoApp();
            }
        });

    }

    private void initElement() {
        managerDB = new ManagerDB(context);
        icGallery = findViewById(R.id.gallery);
        icPhotoSelected = findViewById(R.id.photoSelected);
        icGooglePhoto = findViewById(R.id.googlePhoto);
        icDropbox = findViewById(R.id.dropbox);
        icOther = findViewById(R.id.other);
        icSettings = findViewById(R.id.settings);
        startPresentation = findViewById(R.id.startPresentation);
        infoApp = findViewById(R.id.infoApp);
        showStartPresentationGallery();
        scale = AnimationUtils.loadAnimation(context, R.anim.scale);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_CANCELED) {
            utility.showToast(getString(R.string.no_file_selected), context);
        }else if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            List<String> imagePaths = new ArrayList<>();
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        try {
                            if(imageUri != null) {
                                String url = getRealPathFromURI(imageUri);
                                if(url != null)
                                    imagePaths.add(url);
                            }
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    }
                } else {
                    Uri imageUri = data.getData();
                    try {
                        if(imageUri != null) {
                            String url = getRealPathFromURI(imageUri);
                            if(url != null)
                                imagePaths.add(url);
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }
                saveImages(imagePaths);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            utility.exitAppByOnBackPressed(this);
        } else {
            if(context != null) {
                utility.showToast(getString(R.string.pressToExit), context);
            }
        }
        backPressedTime = System.currentTimeMillis();
    }

    public boolean checkPermission() {
        boolean result = false;
        if(ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String [] {Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }else
            result = true;
        return result;
    }

    public void saveImages(List<String> imagePaths) {
        boolean errorSave = false;
        if(imagePaths.size() > 0) {
            for(String imagePath: imagePaths) {
                if(imagePath != null) {
                    ImageBean imageBean = new ImageBean();
                    imageBean.setImagePath(imagePath);
                    managerDB.openWriteDB();
                    long id = managerDB.insert(imageBean);
                    if(id == -1 && !errorSave)
                        errorSave = true;
                    managerDB.close();
                }
            }
        }
        if(errorSave) {
            utility.showToast(getString(R.string.save_error), context);
        }
        else {
            utility.showToast(getString(R.string.save_successful), context);
        }
        showStartPresentationGallery();
    }

    public void showStartPresentationGallery() {
        if(findElementsOnDB()) {
            icPhotoSelected.setVisibility(View.VISIBLE);
            startPresentation.setVisibility(View.VISIBLE);
        }else {
            icPhotoSelected.setVisibility(View.GONE);
            startPresentation.setVisibility(View.GONE);
        }
    }

    private boolean findElementsOnDB() {
        boolean result = false;
        managerDB.openReadDB();
        List<ImageBean> imageBeans = utility.getListImagesFromDB(managerDB.getAllImages());
        managerDB.close();
        if(imageBeans != null && imageBeans.size() > 0)
            result = true;
        return result;
    }

    //THIS METHOD TO GET THE ACTUAL PATH
    public String getRealPathFromURI(Uri uri) {
        String str = null;
        String[] strings = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, strings, null, null, null);
        if(cursor != null) {
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            cursor.moveToFirst();
            str = cursor.getString(idx);
            cursor.close();
        }
        return str;
    }

    //Shows the welcome alert
    private void showWelcomeAlert(){
        SharedPreferences prefs;
        if(context != null) {
            prefs = context.getSharedPreferences(Utility.PREFS_NAME_FILE, Context.MODE_PRIVATE);
            firstStart = prefs.getBoolean("firstStart", true);
        }
        if(context != null && firstStart){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View viewSaveDialog = View.inflate(context, R.layout.dialog_welcome, null);
            builder.setCancelable(false);
            builder.setView(viewSaveDialog);
            Button buttonClose = viewSaveDialog.findViewById(R.id.btn_delete);
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
            buttonClose.startAnimation(slideDown);
            final AlertDialog dialog = builder.create();
            if(dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
            buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if(activity != null){
                        SharedPreferences.Editor editor = activity.getSharedPreferences(Utility.PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
                        editor.putBoolean("firstStart", false);
                        editor.apply();
                    }
                    showUsageInfoAlert();
                }
            });
            dialog.show();
        }
    }

    //Shows the welcome alert
    private void showUsageInfoAlert(){

        SharedPreferences prefs;
        if(context != null) {
            prefs = context.getSharedPreferences(Utility.PREFS_NAME_FILE, Context.MODE_PRIVATE);
            firstStart = prefs.getBoolean("show_tutorial", true);
        }
        if(context != null && firstStart){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View viewSaveDialog = View.inflate(context, R.layout.dialog_usage_information, null);
            builder.setCancelable(false);
            builder.setView(viewSaveDialog);
            Button buttonClose = viewSaveDialog.findViewById(R.id.btn_close);
            final Button buttonNext = viewSaveDialog.findViewById(R.id.btn_next);
            final Button buttonBack = viewSaveDialog.findViewById(R.id.btn_back);
            final TextView textInfo = viewSaveDialog.findViewById(R.id.text_info);
            final LinearLayout linearLayout = viewSaveDialog.findViewById(R.id.linear_no_show_again);
            final CheckBox showAgainCheck = viewSaveDialog.findViewById(R.id.show_again_check);
            Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
            final Animation rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            buttonClose.startAnimation(slideDown);
            final AlertDialog dialog = builder.create();
            if(dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
            buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if(activity != null && showAgainCheck.isChecked()){
                        SharedPreferences.Editor editor = activity.getSharedPreferences(Utility.PREFS_NAME_FILE, Context.MODE_PRIVATE).edit();
                        editor.putBoolean("show_tutorial", false);
                        editor.apply();
                    }
                }
            });

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textInfo.setText(R.string.text_sec_usage_info);
                    buttonNext.setVisibility(View.GONE);
                    buttonBack.setVisibility(View.VISIBLE);
                    buttonBack.startAnimation(rotate);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            });

            buttonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textInfo.setText(R.string.text_first_usage_info);
                    buttonBack.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    buttonNext.setVisibility(View.VISIBLE);
                    buttonNext.startAnimation(rotate);
                }
            });

            dialog.show();
        }
    }

    //Popup to show info app.wiseframe
    public void showDialogInfoApp() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        final View view = View.inflate(context, R.layout.dialog_info_app, null);
        builder.setCancelable(false);
        builder.setView(view);
        TextView appNameTxt = view.findViewById(R.id.appNameTxt);
        final TextView termsService = view.findViewById(R.id.termsService);
        final TextView privacyPolicy = view.findViewById(R.id.privacyPolicy);
        appNameTxt.getPaint().setShader(new LinearGradient(0f, 0f, appNameTxt.getPaint().measureText(appNameTxt.getText().toString()),
                appNameTxt.getTextSize(), getResources().getColor(R.color.colorStartGradient),
                getResources().getColor(R.color.colorEndGradient), Shader.TileMode.REPEAT));
        Button close = view.findViewById(R.id.btn_close);
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        close.startAnimation(slideDown);

        final android.app.AlertDialog dialog = builder.create();
        if(dialog != null) {
            if(dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog != null)
                    dialog.dismiss();
            }
        });

        termsService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                termsService.startAnimation(scale);
                final String url = "https://wiseframe.netlify.app/terms-conditions.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacyPolicy.startAnimation(scale);
                final String url = "https://wiseframe.netlify.app/privacy-policy.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        if(dialog != null)
            dialog.show();
    }

}
