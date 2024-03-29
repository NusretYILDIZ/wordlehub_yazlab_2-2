package com.yildizsoft.onlinewordle;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity
{
    protected Thread splashMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Thread(new WordleClient("192.168.0.103", 65535)).start();

        splashMain = new Thread(new SplashScreenRunnable(this));
        splashMain.start();
    }

    public void GoToLoginActivity()
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void ConnectionErrorDialog()
    {
        new ConnectionErrorBox(splashMain, this).show(getSupportFragmentManager(), "");
    }
}