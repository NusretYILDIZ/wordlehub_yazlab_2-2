package com.yildizsoft.onlinewordle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import org.jetbrains.annotations.NotNull;

public class ConnectionErrorBox extends DialogFragment
{
    private       Thread               splashMain;
    private final SplashScreenActivity splashScreenActivity;

    public ConnectionErrorBox(Thread splashMain, SplashScreenActivity splashScreenActivity)
    {
        this.splashMain = splashMain;
        this.splashScreenActivity = splashScreenActivity;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Sunucuya bağlanırken bir hata oluştu.");
        builder.setPositiveButton("Tekrar Dene", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                splashMain = new Thread(new SplashScreenRunnable(splashScreenActivity));
                splashMain.start();
            }
        });
        builder.setNegativeButton("Çık", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                WordleClient.StopClient();
                splashScreenActivity.runOnUiThread(splashScreenActivity::finish);
            }
        });
        return builder.create();
    }
}