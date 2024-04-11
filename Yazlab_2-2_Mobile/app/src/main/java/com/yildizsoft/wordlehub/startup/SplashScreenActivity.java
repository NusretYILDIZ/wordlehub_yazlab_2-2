package com.yildizsoft.wordlehub.startup;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.yildizsoft.wordlehub.R;

public class SplashScreenActivity extends AppCompatActivity
{
    //protected Thread splashMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable splashEnder = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), ServerSelectActivity.class));
                finish();
            }
        };

        handler.postDelayed(splashEnder, 2000);

        /*new Thread(new WordleClient("192.168.0.103", 65535)).start();

        splashMain = new Thread(new ServerSelectRunnable(this));
        splashMain.start();
    }

    public void GoToLoginActivity()
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void ConnectionErrorDialog()
    {
        new ConnectionErrorBox(splashMain, this).show(getSupportFragmentManager(), "");*/
    }
}