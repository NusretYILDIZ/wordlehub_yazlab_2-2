package com.yildizsoft.wordlehub.game.online.gameplay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.widget.Button;
import com.yildizsoft.wordlehub.game.online.lobby.ProcessGameRequestRunnable;

public class RematchOfferReceivedDialog
{
    private final GuessWordActivity guessWordActivity;
    private final String            username;
    
    public RematchOfferReceivedDialog(GuessWordActivity guessWordActivity, String username)
    {
        this.guessWordActivity = guessWordActivity;
        this.username          = username;
    }
    
    public void Show()
    {
        CountDownTimer[] timer = new CountDownTimer[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(guessWordActivity);
        
        builder.setMessage('"' + this.username + "\" size düello teklif etti.");
        
        builder.setPositiveButton("Kabul Et", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO: Accept the invitation.
                new Thread(new ProcessRematchOfferRunnable(guessWordActivity, true)).start();
                timer[0].cancel();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Reddet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // TODO: Reject the invitation.
                new Thread(new ProcessRematchOfferRunnable(guessWordActivity, false)).start();
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
                        new Thread(new ProcessRematchOfferRunnable(guessWordActivity, false)).start();
                        dialog.dismiss();
                    }
                }.start();
            }
        });
        
        dialog.show();
    }
}
