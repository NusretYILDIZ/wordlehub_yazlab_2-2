package org.yildizsoft;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class OnlinePlayers
{
    private static ArrayList<PlayerInfo> players = new ArrayList<>();
    private static Semaphore playerMutex = new Semaphore(1);
    
    public static void NewOnlinePlayer(PlayerInfo player)
    {
        try
        {
            playerMutex.acquire();
            players.add(player);
            playerMutex.release();
            System.out.println('"' + player.getUsername() + "\" isimli oyuncu çevrimiçi oldu.");
        }
        catch(InterruptedException e)
        {
            System.err.println("OnlinePlayers.NewOnlinePlayer fonksiyonu kesintiye uğradı.\n" + e);
        }
    }
    
    public static void RemoveOnlinePlayer(PlayerInfo player)
    {
        try
        {
            playerMutex.acquire();
            if(players.remove(player)) System.out.println('"' + player.getUsername() + "\" isimli oyuncu çevrimdışı oldu.");
            else System.out.println('"' + player.getUsername() + "\" isimli oyuncu çevrimiçi oyuncular listesinde bulunamadı.");
            playerMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("OnlinePlayers.RemoveOnlinePlayer fonksiyonu kesintiye uğradı.\n" + e);
        }
    }
    
    public static void RemoveOnlinePlayer(String id)
    {
        try
        {
            playerMutex.acquire();
            for(int i = players.size() - 1; i >= 0; i--)
            {
                if(players.get(i).getId().equals(id))
                {
                    System.out.println('"' + players.get(i).getUsername() + "\" isimli oyuncu çevrimdışı oldu.");
                    players.remove(i);
                    break;
                }
            }
            playerMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("OnlinePlayers.RemoveOnlinePlayer fonksiyonu kesintiye uğradı.\n" + e);
        }
    }
    
    public static void SetLobbyOfPlayer(PlayerInfo player, PlayerLobby lobby)
    {
        try
        {
            playerMutex.acquire();
            if(player != null /*&& IsPlayerOnline(player.getUsername())*/) players.get(players.indexOf(player)).setLobby(lobby);
            playerMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("OnlinePlayers.SetLobbyOfPlayer fonksiyonu kesintiye uğradı.\n" + e);
        }
    }
    
    public static PlayerInfo GetOnlinePlayerByName(String username)
    {
        PlayerInfo playerInfo = null;
        
        try
        {
            playerMutex.acquire();
            for(PlayerInfo player : players)
            {
                if(player.getUsername().equals(username))
                {
                    playerInfo = player;
                    break;
                }
            }
            playerMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("OnlinePlayers.GetOnlinePlayerByName fonksiyonu kesintiye uğradı.\n" + e);
        }
        
        return playerInfo;
    }
    
    public static PlayerInfo GetOnlinePlayerByID(String id)
    {
        PlayerInfo playerInfo = null;
        
        try
        {
            playerMutex.acquire();
            for(PlayerInfo player : players)
            {
                if(player.getId().equals(id))
                {
                    playerInfo = player;
                    break;
                }
            }
            playerMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("OnlinePlayers.GetOnlinePlayerByID fonksiyonu kesintiye uğradı.\n" + e);
        }
        
        return playerInfo;
    }
    
    public static boolean IsPlayerOnline(String username)
    {
        return GetOnlinePlayerByName(username) != null;
    }
    
    public static ArrayList<PlayerInfo> GetPlayersInLobby(PlayerLobby lobby)
    {
        ArrayList<PlayerInfo> playersInLobby = new ArrayList<>();
        List<PlayerInfo> onlinePlayers = null;
        
        try
        {
            playerMutex.acquire();
            onlinePlayers = new ArrayList<>(players);
            playerMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("OnlinePlayers.GetOnlinePlayerByID fonksiyonu kesintiye uğradı.\n" + e);
        }
        
        if(onlinePlayers == null)
        {
            System.err.println("Online oyuncular listesi kopyalanamadı.");
            return null;
        }
        
        for(PlayerInfo player : onlinePlayers)
        {
            if(player.getLobby() == lobby) playersInLobby.add(player);
        }
        
        return playersInLobby;
    }
}
