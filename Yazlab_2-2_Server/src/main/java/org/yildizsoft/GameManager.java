package org.yildizsoft;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class GameManager implements Runnable
{
    private PlayerInfo player1, player2;
    private static boolean shouldRun = true;
    
    ScheduledExecutorService scheduledExecutorService;
    ScheduledFuture<?> scheduledFuture;
    
    public GameManager(PlayerInfo player1, PlayerInfo player2)
    {
        this.player1 = player1;
        this.player2 = player2;
    }
    
    @Override
    public void run()
    {
        ClientTask.AddNewTask(new ClientTask(this.player1.getId(), "START_GAME"));
        ClientTask.AddNewTask(new ClientTask(this.player2.getId(), "START_GAME"));
        
        shouldRun = true;
        
        while(shouldRun)
        {
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
