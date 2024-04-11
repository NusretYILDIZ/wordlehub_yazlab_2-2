package com.yildizsoft.wordlehub.game.online;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.yildizsoft.wordlehub.R;
import com.yildizsoft.wordlehub.client.PlayerLobby;
import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.login.LoginActivity;

public class GameSelectActivity extends AppCompatActivity
{
    public String constLetterMode = "";
    public String letterCount = "";
    private GameSelectActivity gameSelectActivity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);
        
        gameSelectActivity = this;
        
        LinearLayout gamemodeSelectView = findViewById(R.id.gamemodeSelectView);
        Button normalModeButton = findViewById(R.id.normalModeButton);
        Button constLetterModeButton = findViewById(R.id.constLetterModeButton);
        
        LinearLayout wordLengthSelectView = findViewById(R.id.wordLengthSelectView);
        Button word4LetterButton = findViewById(R.id.word4LetterButton);
        Button word5LetterButton = findViewById(R.id.word5LetterButton);
        Button word6LetterButton = findViewById(R.id.word6LetterButton);
        Button word7LetterButton = findViewById(R.id.word7LetterButton);
        TextView openGameModeSelectText = findViewById(R.id.openGameModeSelectText);
        
        normalModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                constLetterMode = "NO";
                gamemodeSelectView.setVisibility(View.GONE);
                wordLengthSelectView.setVisibility(View.VISIBLE);
            }
        });
        
        constLetterModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                constLetterMode = "WITH";
                gamemodeSelectView.setVisibility(View.GONE);
                wordLengthSelectView.setVisibility(View.VISIBLE);
            }
        });
        
        openGameModeSelectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                constLetterMode = "";
                wordLengthSelectView.setVisibility(View.GONE);
                gamemodeSelectView.setVisibility(View.VISIBLE);
            }
        });
        
        word4LetterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                letterCount = "4";
                System.out.println(constLetterMode + "_CONST_" + letterCount + "_LETTER");
                new Thread(new GameSelectRunnable(gameSelectActivity, constLetterMode, letterCount)).start();
            }
        });
        
        word5LetterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                letterCount = "5";
                System.out.println(constLetterMode + "_CONST_" + letterCount + "_LETTER");
                new Thread(new GameSelectRunnable(gameSelectActivity, constLetterMode, letterCount)).start();
            }
        });
        
        word6LetterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                letterCount = "6";
                System.out.println(constLetterMode + "_CONST_" + letterCount + "_LETTER");
                new Thread(new GameSelectRunnable(gameSelectActivity, constLetterMode, letterCount)).start();
            }
        });
        
        word7LetterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                letterCount = "7";
                System.out.println(constLetterMode + "_CONST_" + letterCount + "_LETTER");
                new Thread(new GameSelectRunnable(gameSelectActivity, constLetterMode, letterCount)).start();
            }
        });
    }
    
    public void GoToLobby()
    {
        WordleClient.SetPlayerLobby(PlayerLobby.valueOf(constLetterMode + "_CONST_" + letterCount + "_LETTER"));
        startActivity(new Intent(getApplicationContext(), LobbyActivity.class));
        finish();
    }
    
    public void GoToLogin()
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}