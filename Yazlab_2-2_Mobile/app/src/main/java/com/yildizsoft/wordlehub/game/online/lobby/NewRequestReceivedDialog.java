package com.yildizsoft.wordlehub.game.online.lobby;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.widget.Button;

public class NewRequestReceivedDialog
{
    private final LobbyActivity lobbyActivity;
    private final String username;
    
    public NewRequestReceivedDialog(LobbyActivity lobbyActivity, String username)
    {
        this.lobbyActivity = lobbyActivity;
        this.username = username;
    }
    
    public void Show()
    {
        CountDownTimer[] timer = new CountDownTimer[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(lobbyActivity);
        
        builder.setTitle("Yeni Oyun İsteği");
        builder.setMessage('"' + this.username + "\" size oyun daveti gönderdi.");
        
        builder.setPositiveButton("Kabul Et", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO: Accept the invitation.
                new Thread(new ProcessGameRequestRunnable(lobbyActivity, username, true)).start();
                timer[0].cancel();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Reddet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO: Reject the invitation.
                new Thread(new ProcessGameRequestRunnable(lobbyActivity, username, false)).start();
                timer[0].cancel();
                dialog.dismiss();
            }
        });
        
        AlertDialog dialog = builder.create();
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog)
            {
                final Button rejectButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                
                timer[0] = new CountDownTimer(10000, 500)
                {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        rejectButton.setText("Reddet (" + millisUntilFinished / 1000 + ")");
                    }
                    
                    @Override
                    public void onFinish()
                    {
                        // TODO: Reject the invitation.
                        new Thread(new ProcessGameRequestRunnable(lobbyActivity, username, false)).start();
                        dialog.dismiss();
                    }
                }.start();
            }
        });
        
        dialog.show();
    }
}
