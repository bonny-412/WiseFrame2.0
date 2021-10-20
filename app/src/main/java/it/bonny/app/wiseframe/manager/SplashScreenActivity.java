package it.bonny.app.wiseframe.manager;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import it.bonny.app.wiseframe.R;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
        ImageView logo = findViewById(R.id.idLogo);
        TextView textLogo = findViewById(R.id.textLogo);
        Animation zoom_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        Animation move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
        textLogo.getPaint().setShader(new LinearGradient(0f, 0f, textLogo.getPaint().measureText(textLogo.getText().toString()),
                textLogo.getTextSize(), getResources().getColor(R.color.colorStartGradient),
                getResources().getColor(R.color.colorEndGradient), Shader.TileMode.REPEAT));
        textLogo.startAnimation(move);
        zoom_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                overridePendingTransition(R.anim.animation_leave, R.anim.animation_enter);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        logo.startAnimation(zoom_in);
    }
}
