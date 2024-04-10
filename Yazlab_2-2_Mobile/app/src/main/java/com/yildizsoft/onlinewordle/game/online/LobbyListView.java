package com.yildizsoft.onlinewordle.game.online;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yildizsoft.onlinewordle.R;
import com.yildizsoft.onlinewordle.client.PlayerInfo;
import com.yildizsoft.onlinewordle.client.PlayerStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LobbyListView extends RecyclerView.Adapter<LobbyListView.LobbyViewHolder>
{
    private LayoutInflater layoutInflater;
    private List<PlayerInfo> playerList;
    
    public LobbyListView(Context context, List<PlayerInfo> playerList)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.playerList = playerList;
    }
    
    @NonNull
    @NotNull
    @Override
    public LobbyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i)
    {
        return new LobbyViewHolder(layoutInflater.inflate(R.layout.lobby_list_item, viewGroup, false));
    }
    
    @Override
    public void onBindViewHolder(@NonNull @NotNull LobbyViewHolder lobbyViewHolder, int i)
    {
        PlayerInfo player = playerList.get(i);
        lobbyViewHolder.usernameText.setText(player.getUsername());
        
        if(player.getStatus() == PlayerStatus.IN_GAME)
        {
            lobbyViewHolder.playerStatusText.setText(R.string.in_game_text);
            lobbyViewHolder.playerStatusText.setTextColor(0xFFFF0000);
            lobbyViewHolder.sendRequestButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            lobbyViewHolder.playerStatusText.setText(R.string.online_text);
            lobbyViewHolder.playerStatusText.setTextColor(0xFF009900);
            lobbyViewHolder.sendRequestButton.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public int getItemCount()
    {
        return playerList.size();
    }
    
    public static class LobbyViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView usernameText;
        private final TextView playerStatusText;
        private final Button sendRequestButton;
        
        public LobbyViewHolder(@NonNull @NotNull View itemView)
        {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            playerStatusText = itemView.findViewById(R.id.playerStatusText);
            sendRequestButton = itemView.findViewById(R.id.sendRequestButton);
            
            sendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    System.out.println("Send game request to " + usernameText);
                    // TODO: Implement sending actual game requests.
                }
            });
        }
    }
}
