package org.yildizsoft;

public class PlayerInfo
{
    private final String         id;
    private final String       username;
    private       PlayerStatus status;
    private       PlayerLobby  lobby;
    private       String       playingWith;
    
    public PlayerInfo(String id, String username/*, PlayerStatus status, PlayerLobby lobby*/)
    {
        this.id          = id;
        this.username    = username;
        this.status      = PlayerStatus.ONLINE; //status;
        this.lobby       = null; //lobby;
        this.playingWith = null;
    }
    
    public String getId()
    {
        return id;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public PlayerStatus getStatus()
    {
        return status;
    }
    
    public void setStatus(PlayerStatus status)
    {
        this.status = status;
    }
    
    public PlayerLobby getLobby()
    {
        return lobby;
    }
    
    public void setLobby(PlayerLobby lobby)
    {
        this.lobby = lobby;
    }
    
    public String getPlayingWith()
    {
        return playingWith;
    }
    
    public void setPlayingWith(String playingWith)
    {
        this.playingWith = playingWith;
    }
}
