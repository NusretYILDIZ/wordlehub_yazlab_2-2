package com.yildizsoft.onlinewordle.startup;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.yildizsoft.onlinewordle.R;
import com.yildizsoft.onlinewordle.client.WordleClient;
import com.yildizsoft.onlinewordle.login.LoginActivity;

public class ServerSelectActivity extends AppCompatActivity
{
    protected Thread splashMain;
    protected WaitConnectionDialog waitConnectionDialog;
    protected ServerSelectActivity serverSelectActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_select);

        Button playOnline = findViewById(R.id.playOnline);
        Button playOffline = findViewById(R.id.playOffline);

        serverSelectActivity = this;
        new Thread(new WordleClient()).start();

        playOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new ServerAddressDialog(serverSelectActivity).show(getSupportFragmentManager(), "server_browser");
                
                /*waitConnectionDialog = new WaitConnectionDialog(serverSelectActivity);
                waitConnectionDialog.Show();

                new Thread(new WordleClient("192.168.0.104", 65535)).start();

                splashMain = new Thread(new ServerSelectRunnable(serverSelectActivity));
                splashMain.start();*/
            }
        });

        playOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                OfflineModeNotImplementedDialog();
            }
        });
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ServerSelectRunnable.Stop();
    }
    
    public void ConnectToTheServer(String ip, int port)
    {
        waitConnectionDialog = new WaitConnectionDialog(serverSelectActivity);
        waitConnectionDialog.Show();
        
        WordleClient.SetServerIpPort(ip, port);
        
        splashMain = new Thread(new ServerSelectRunnable(serverSelectActivity, ip, port));
        splashMain.start();
    }
    
    public void RetryConnecting()
    {
        new ServerAddressDialog(serverSelectActivity).show(getSupportFragmentManager(), "server_browser");
        /*waitConnectionDialog.Show();
        splashMain = new Thread(new ServerSelectRunnable(serverSelectActivity));
        splashMain.start();*/
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

    public void OfflineModeNotImplementedDialog()
    {
        new NoOfflineModeDialog().show(getSupportFragmentManager(), "");
    }

    public void DismissWaitDialog()
    {
        waitConnectionDialog.Dismiss();
    }
}