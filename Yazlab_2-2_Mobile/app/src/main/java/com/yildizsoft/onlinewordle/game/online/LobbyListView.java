package com.yildizsoft.onlinewordle.game.online;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yildizsoft.onlinewordle.R;
import org.jetbrains.annotations.NotNull;

public class LobbyListView extends RecyclerView.Adapter<LobbyListView.LobbyViewHolder>
{
    private LayoutInflater layoutInflater;
    
    public LobbyListView()
    {
    
    }
    
    @NonNull
    @NotNull
    @Override
    public LobbyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i)
    {
        return null;
    }
    
    @Override
    public void onBindViewHolder(@NonNull @NotNull LobbyViewHolder lobbyViewHolder, int i)
    {
    
    }
    
    @Override
    public int getItemCount()
    {
        return 0;
    }
    
    public static class LobbyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
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
            itemView.setOnClickListener(this);
        }
        
        @Override
        public void onClick(View v)
        {
        
        }
    }
    
    public interface ClickListener
    {
        void OnClick(View view, int pos);
    }
}
