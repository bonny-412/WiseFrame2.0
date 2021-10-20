package it.bonny.app.wiseframe.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.List;

import it.bonny.app.wiseframe.R;
import it.bonny.app.wiseframe.bean.ImageBean;
import it.bonny.app.wiseframe.bean.SettingsBean;
import it.bonny.app.wiseframe.db.ManagerDB;
import it.bonny.app.wiseframe.util.Utility;

public class GalleryActivity extends AppCompatActivity {
    private final Activity activity = this;
    private final Context context = this;
    private GridView gallery;
    private ManagerDB managerDB = null;
    private final Utility utility = new Utility();
    private List<ImageBean> imageBeans;
    private GridAdapter gridAdapter;
    private boolean clickBtnSnackbar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsBean settingsBean = utility.getSettingBeanSaved(activity);
        if(settingsBean != null){
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(settingsBean.isActiveAnalytics());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, savedInstanceState);
        }
        setContentView(R.layout.activity_gallery);
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.toolbar_action_bar));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initElement();
        managerDB.openReadDB();
        imageBeans = utility.getListImagesFromDB(managerDB.getAllImages());
        managerDB.close();
        gridAdapter = new GridAdapter(this, imageBeans);
        gallery.setAdapter(gridAdapter);
        gridAdapter.notifyDataSetChanged();
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Animation scale = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
                view.setAnimation(scale);
                showDialogBox(position);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(menuItem);
    }

    private void initElement() {
        managerDB = new ManagerDB(getApplicationContext());
        gallery = findViewById(R.id.imageGallery);
    }

    public void showDialogBox(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = View.inflate(context, R.layout.dialog_image_gallery, null);
        builder.setCancelable(false);
        builder.setView(view);
        TextView title = view.findViewById(R.id.image_name);
        ImageView img = view.findViewById(R.id.img);
        Button delete = view.findViewById(R.id.btn_delete);
        Button close = view.findViewById(R.id.btn_close);
        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        delete.startAnimation(slideDown);
        close.startAnimation(slideDown);
        String name = getString(R.string.image_title_dialog_box) + " " + position;
        title.setText(name);

        boolean fileExists = true;
        try {
            File file = new File(imageBeans.get(position).getImagePath());
            if(!file.exists())
                fileExists = false;
        }catch (Exception e) {
            fileExists = false;
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        if(fileExists)
            Glide.with(activity).load(imageBeans.get(position).getImagePath()).into(img);
        else {
            imageBeans.remove(position);
            gridAdapter.notifyDataSetChanged();
            deleteImage(imageBeans.get(position));
        }

        final AlertDialog dialog = builder.create();
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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog != null) {
                    dialog.dismiss();
                    final ImageBean imageBeanDelete = imageBeans.get(position);
                    imageBeans.remove(position);
                    gridAdapter.notifyDataSetChanged();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.imageDeleted), BaseTransientBottomBar.LENGTH_LONG)
                            .setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickBtnSnackbar = true;
                                    imageBeans.add(position, imageBeanDelete);
                                    gridAdapter.notifyDataSetChanged();
                                }
                            });
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            if(!clickBtnSnackbar) {
                                deleteImage(imageBeanDelete);
                            }
                        }
                    });
                    snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.WHITE);
                    TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    textView.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
        if(dialog != null)
            dialog.show();
    }

    private void deleteImage(ImageBean imageBeanDelete) {
        managerDB.openWriteDB();
        boolean result = managerDB.deleteById(imageBeanDelete.getId());
        managerDB.close();
        if(!result)
            FirebaseCrashlytics.getInstance().log("Delete image by id error");
    }

}