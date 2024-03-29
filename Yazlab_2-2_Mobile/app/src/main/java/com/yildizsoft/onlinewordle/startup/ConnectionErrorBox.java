package com.yildizsoft.onlinewordle.startup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.yildizsoft.onlinewordle.client.WordleClient;
import org.jetbrains.annotations.NotNull;

public class ConnectionErrorBox extends DialogFragment
{
    private       Thread               splashMain;
    private final ServerSelectActivity serverSelectActivity;

    public ConnectionErrorBox(Thread splashMain, ServerSelectActivity serverSelectActivity)
    {
        this.splashMain           = splashMain;
        this.serverSelectActivity = serverSelectActivity;
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
                //splashMain = new Thread(new ServerSelectRunnable(serverSelectActivity));
                //splashMain.start();
                serverSelectActivity.runOnUiThread(serverSelectActivity::RetryConnecting);
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                WordleClient.StopClient();
                //splashScreenActivity.runOnUiThread(splashScreenActivity::finish);
            }
        });
        setCancelable(false);
        return builder.create();
    }
}