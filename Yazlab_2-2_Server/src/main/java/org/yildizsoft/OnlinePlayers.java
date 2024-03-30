package org.yildizsoft;

import java.util.ArrayList;

public class OnlinePlayers
{
    private static ArrayList<PlayerInfo> players = new ArrayList<>();
    
    public static void NewOnlinePlayer(PlayerInfo player)
    {
        players.add(player);
        System.out.println('"' + player.getUsername() + "\" isimli oyuncu çevrimiçi oldu.");
    }
    
    public static void RemoveOnlinePlayer(PlayerInfo player)
    {
        if(players.remove(player)) System.out.println('"' + player.getUsername() + "\" isimli oyuncu çevrimdışı oldu.");
        else System.out.println('"' + player.getUsername() + "\" isimli oyuncu çevrimiçi oyuncular listesinde bulunamadı.");
    }
    
    public static void RemoveOnlinePlayer(long id)
    {
        for(int i = players.size() - 1; i >= 0; i--)
        {
            if(players.get(i).getId() == id)
            {
                System.out.println('"' + players.get(i).getUsername() + "\" isimli oyuncu çevrimdışı oldu.");
                players.remove(i);
                return;
            }
        }
        
        System.err.println(id + " ID'li oyuncu çevrimiçi oyuncular listesinde bulunamadı.");
    }
    
    public static PlayerInfo GetOnlinePlayer(String username)
    {
        for(PlayerInfo player : players)
        {
            if(player.getUsername().equals(username)) return player;
        }
        
        return null;
    }
    
    public static boolean IsPlayerOnline(String username)
    {
        return GetOnlinePlayer(username) != null;
    }
    
    public static ArrayList<PlayerInfo> GetPlayersInLobby(PlayerLobby lobby)
    {
        ArrayList<PlayerInfo> playersInLobby = new ArrayList<>();
        
        for(PlayerInfo player : players)
        {
            if(player.getLobby() == lobby) playersInLobby.add(player);
        }
        
        return playersInLobby;
    }
}
