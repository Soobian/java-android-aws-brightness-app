package com.example.brightnessapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.brightnessapp.databinding.ActivityDashboardBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#6dd5ed"));
        Thread splash = new Thread() {
            public void run() {
                try {
                    StartAnimations();
                    sleep(1000);
                    Intent dashboard = new Intent(getBaseContext(), DashboardActivity.class);
                    startActivity(dashboard);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        splash.start();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        ConstraintLayout l = (ConstraintLayout) findViewById(R.id.backGround);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.main);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        TextView splashBanTxt = (TextView) findViewById(R.id.textView);
        TextView splashTitle = (TextView) findViewById(R.id.textView2);

        iv.clearAnimation();
        iv.startAnimation(anim);

        splashBanTxt.clearAnimation();
        splashBanTxt.startAnimation(anim);

        splashTitle.clearAnimation();
        splashTitle.startAnimation(anim);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}