package com.yildizsoft.wordle;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        WordleClient.SetFragmentManager(getSupportFragmentManager());

        new Thread(new WordleClient("192.168.0.103", 65535)).start();

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable splashEnder = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        };

        handler.postDelayed(splashEnder, 2000);
    }
}