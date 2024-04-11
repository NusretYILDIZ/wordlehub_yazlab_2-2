package com.yildizsoft.wordlehub.game.online;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.yildizsoft.wordlehub.R;
import com.yildizsoft.wordlehub.client.*;
import com.yildizsoft.wordlehub.login.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyActivity extends AppCompatActivity
{
    LobbyListView lobbyListView;
    List<PlayerInfo> playerInfoList = new ArrayList<>();
    RecyclerView recycledLobbyView;
    Context context;
    Thread lobbyThread;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        
        Button backToGameSelectButton = findViewById(R.id.backToGameSelectButton);
        Button logoutButton = findViewById(R.id.logoutButton);
        
        backToGameSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                WordleClient.AddNewTask(new WordleTask(WordleTaskType.EXIT_LOBBY, null));
                LobbyRunnable.Stop();
                startActivity(new Intent(getApplicationContext(), GameSelectActivity.class));
                finish();
            }
        });
        
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                WordleClient.AddNewTask(new WordleTask(WordleTaskType.LOGOUT, null));
                LobbyRunnable.Stop();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
        
        if(WordleClient.GetCurrentPlayer() != null)
        {
            TextView lobbyNameText = findViewById(R.id.lobbyNameText);
            String lobbyName = "";
            
            switch(WordleClient.GetCurrentPlayer().getLobby())
            {
            case NO_CONST_4_LETTER:
                lobbyName = "Normal Mod - 4 Harfli Kelime";
                break;
            
            case NO_CONST_5_LETTER:
                lobbyName = "Normal Mod - 5 Harfli Kelime";
                break;
            
            case NO_CONST_6_LETTER:
                lobbyName = "Normal Mod - 6 Harfli Kelime";
                break;
            
            case NO_CONST_7_LETTER:
                lobbyName = "Normal Mod - 7 Harfli Kelime";
                break;
            
            case WITH_CONST_4_LETTER:
                lobbyName = "Sabit Harfli Mod - 4 Harfli Kelime";
                break;
            
            case WITH_CONST_5_LETTER:
                lobbyName = "Sabit Harfli Mod - 5 Harfli Kelime";
                break;
            
            case WITH_CONST_6_LETTER:
                lobbyName = "Sabit Harfli Mod - 6 Harfli Kelime";
                break;
            
            case WITH_CONST_7_LETTER:
                lobbyName = "Sabit Harfli Mod - 7 Harfli Kelime";
                break;
            }
            
            lobbyNameText.setText(lobbyName);
        }
        
        context = this;
        recycledLobbyView = findViewById(R.id.playerList);
        
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycledLobbyView.setLayoutManager(linearLayoutManager);
        recycledLobbyView.addItemDecoration(new DividerItemDecoration(recycledLobbyView.getContext(), linearLayoutManager.getOrientation()));
        lobbyListView = new LobbyListView(context, playerInfoList);
        recycledLobbyView.setAdapter(lobbyListView);
        
        lobbyThread = new Thread(new LobbyRunnable(this));
        lobbyThread.start();
    }
    
    @SuppressLint("NotifyDataSetChanged")
    public void CreatePlayerList(String[] playersData)
    {
        System.out.println("playersData = " + Arrays.toString(playersData));
        playerInfoList.clear();
        
        for(int i = 0; i < playersData.length; i += 2)
        {
            if(WordleClient.GetCurrentPlayer() != null)
            {
                if(WordleClient.GetCurrentPlayer().getUsername().equals(playersData[i]) || WordleClient.GetCurrentPlayer().getLobby() == null) continue;
            }
            
            playerInfoList.add(new PlayerInfo(playersData[i], PlayerStatus.valueOf(playersData[i + 1])));
        }
        
        if(playerInfoList.isEmpty())
        {
            TextView noPlayersText = findViewById(R.id.noPlayerText);
            noPlayersText.setVisibility(View.VISIBLE);
            recycledLobbyView.setVisibility(View.GONE);
        }
        else
        {
            TextView noPlayersText = findViewById(R.id.noPlayerText);
            noPlayersText.setVisibility(View.GONE);
            recycledLobbyView.setVisibility(View.VISIBLE);
            lobbyListView.notifyDataSetChanged();
        }
    }
    
    public void BackToGameSelect()
    {
        LobbyRunnable.Stop();
        startActivity(new Intent(getApplicationContext(), GameSelectActivity.class));
        finish();
    }
    
    public void CannotExitLobby()
    {
        System.err.println("Cannot exit lobby.");
    }
    
    public void NoPlayers()
    {
        System.out.println("No players.");
        TextView noPlayersText = findViewById(R.id.noPlayerText);
        noPlayersText.setVisibility(View.VISIBLE);
        recycledLobbyView.setVisibility(View.GONE);
    }
    
    public void GoToLogin()
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}