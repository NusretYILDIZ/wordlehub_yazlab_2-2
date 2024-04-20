package com.yildizsoft.wordlehub.game.online.lobby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yildizsoft.wordlehub.R;
import com.yildizsoft.wordlehub.client.PlayerInfo;
import com.yildizsoft.wordlehub.client.PlayerStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LobbyListView extends RecyclerView.Adapter<LobbyListView.LobbyViewHolder>
{
    private LayoutInflater layoutInflater;
    private List<PlayerInfo> playerList;
    private final LobbyActivity lobbyActivity;
    
    public LobbyListView(LobbyActivity lobbyActivity, List<PlayerInfo> playerList)
    {
        this.layoutInflater = LayoutInflater.from(lobbyActivity);
        this.playerList = playerList;
        this.lobbyActivity = lobbyActivity;
    }
    
    @NonNull
    @NotNull
    @Override
    public LobbyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i)
    {
        return new LobbyViewHolder(layoutInflater.inflate(R.layout.lobby_list_item, viewGroup, false), lobbyActivity);
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
        else if(player.getStatus() == PlayerStatus.WAITING_REQUEST)
        {
            lobbyViewHolder.playerStatusText.setText(R.string.waiting_request_text);
            lobbyViewHolder.playerStatusText.setTextColor(0xFFFF7300);
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
        
        public LobbyViewHolder(@NonNull @NotNull View itemView, LobbyActivity lobbyActivity)
        {
            super(itemView);
            this.usernameText = itemView.findViewById(R.id.usernameText);
            this.playerStatusText = itemView.findViewById(R.id.playerStatusText);
            this.sendRequestButton = itemView.findViewById(R.id.sendRequestButton);
            
            sendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    System.out.println("Send game request to " + usernameText.getText().toString());
                    // TODO: Implement sending actual game requests.
                    LobbyActivity.SendGameRequest(lobbyActivity, usernameText.getText().toString());
                }
            });
        }
    }
}
