package com.yildizsoft.onlinewordle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity
{
    protected Thread splashMain;
    protected Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        WordleClient.SetFragmentManager(getSupportFragmentManager());

        new Thread(new WordleClient("192.168.0.103", 65535)).start();

        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(@NonNull Message msg)
            {
                if(msg.what == 0)
                {
                    ConnectionErrorDialog();
                    //finish();
                }
                else if(msg.what == 1)
                {
                    GoToLoginActivity();
                }
                super.handleMessage(msg);
            }
        };

        splashMain = new Thread(new SplashScreenRunnable(this, handler));
        splashMain.start();

        /*Handler handler = new Handler(Looper.getMainLooper());
        Runnable splashEnder = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        };

        handler.postDelayed(splashEnder, 2000);*/
    }

    @Override
    protected void onDestroy()
    {
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.QUIT, null));
        super.onDestroy();
    }

    public void GoToLoginActivity()
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void ConnectionErrorDialog()
    {
        new InfoBox("Sunucuya bağlanırken bir hata oluştu", InfoBox.START_CLIENT_ERROR).show(getSupportFragmentManager(), "");
        /*if(!this.isFinishing())
        {
            new AlertDialog.Builder(getApplicationContext())
                    .setMessage("Sunucuya bağlanırken bir hata oluştu.")
                    .setNegativeButton("Çık", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .setPositiveButton("Tekrar Dene", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            splashMain.start();
                        }
                    })
                    .show();
        }*/
    }
}