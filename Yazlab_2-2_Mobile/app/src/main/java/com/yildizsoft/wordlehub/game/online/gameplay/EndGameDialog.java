package com.yildizsoft.wordlehub.game.online.gameplay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.widget.Button;
import com.yildizsoft.wordlehub.client.WordleClient;

public class EndGameDialog
{
    private final GuessWordActivity guessWordActivity;
    private final String player1Name, player2Name;
    private final int player1Points, player2Points;
    private CountDownTimer timer;
    private AlertDialog dialog;
    private final Boolean p1WonPrematurely;
    
    public EndGameDialog(GuessWordActivity guessWordActivity, String player1Name, String player2Name, int player1Points, int player2Points)
    {
        this.guessWordActivity = guessWordActivity;
        this.p1WonPrematurely = null;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Points = player1Points;
        this.player2Points = player2Points;
    }
    
    public EndGameDialog(GuessWordActivity guessWordActivity, Boolean p1WonPrematurely, String player1Name, String player2Name)
    {
        this.guessWordActivity = guessWordActivity;
        this.p1WonPrematurely = p1WonPrematurely;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.player1Points = 0;
        this.player2Points = 0;
    }
    
    public void Show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(guessWordActivity);
        
        builder.setTitle("Oyun Bitti");
        
        if(p1WonPrematurely != null)
        {
            if(p1WonPrematurely)
                builder.setMessage("Oyunu " + player1Name + " kazandı.\n");
            else
                builder.setMessage("Oyunu " + player2Name + " kazandı.\n");
        }
        else
        {
            if(player1Points > player2Points)
                builder.setMessage("Oyunu" + player1Name + "kazandı.\n\n" + player1Name + " isimli oyuncu " + player1Points + " puana sahip.\n" + player2Name + " isimli oyuncu " + player2Points + " puana sahip.\n");
            else if(player1Points < player2Points)
                builder.setMessage("Oyunu" + player2Name + "kazandı.\n\n" + player1Name + " isimli oyuncu " + player1Points + " puana sahip.\n" + player2Name + " isimli oyuncu " + player2Points + " puana sahip.\n");
            else
                builder.setMessage("Oyun berabere bitti.\n\n" + player1Name + " isimli oyuncu " + player1Points + " puana sahip.\n" + player2Name + " isimli oyuncu " + player2Points + " puana sahip.\n");
        }
        
        builder.setPositiveButton("Düello Teklif Et", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //new Thread(new ProcessGameRequestRunnable(guessWordActivity, username, true)).start();
                timer.cancel();
                guessWordActivity.runOnUiThread(() -> guessWordActivity.OfferRematch(player1Name, player2Name));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Odaya Dön", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //new Thread(new ProcessGameRequestRunnable(guessWordActivity, username, false)).start();
                timer.cancel();
                guessWordActivity.runOnUiThread(guessWordActivity::GoToLobbyRequest);
                dialog.dismiss();
            }
        });
        
        dialog = builder.create();
        
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog)
            {
                final Button rejectButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                
                timer = new CountDownTimer(30000, 500)
                {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        rejectButton.setText("Odaya Dön (" + millisUntilFinished / 1000 + ")");
                    }
                    
                    @Override
                    public void onFinish()
                    {
                        //new Thread(new ProcessGameRequestRunnable(guessWordActivity, username, false)).start();
                        guessWordActivity.runOnUiThread(guessWordActivity::GoToLobby);
                        dialog.dismiss();
                    }
                }.start();
            }
        });
        
        dialog.show();
    }
    
    public void Dismiss()
    {
        timer.cancel();
        dialog.dismiss();
    }
}
