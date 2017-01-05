package com.example.simpletwitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Splash screen
 * Starting screen that contains an image view
 * with frequently fade-in and fade-out animation.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView ivSplash = (ImageView) findViewById(R.id.ivSplash);

        final Animation fade_in = AnimationUtils.loadAnimation(this,R.anim.anim_splash_image);
        ivSplash.setAnimation(fade_in);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, FollowersListActivity.class));
                finish();
            }
        }, 5000);
    }
}
