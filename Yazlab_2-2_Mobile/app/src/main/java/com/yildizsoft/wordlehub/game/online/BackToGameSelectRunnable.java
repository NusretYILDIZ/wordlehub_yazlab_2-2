package com.yildizsoft.wordlehub.game.online;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

public class BackToGameSelectRunnable implements Runnable
{
    private final LobbyActivity lobbyActivity;
    private static boolean shouldRun = true;
    
    public BackToGameSelectRunnable(LobbyActivity lobbyActivity)
    {
        this.lobbyActivity = lobbyActivity;
    }
    
    @Override
    public void run()
    {
        System.out.println("[BackToGameSelectRunnable] Start of run function.");
        
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.EXIT_LOBBY, null));
        if(taskID == -1) System.err.println("[BackToGameSelectRunnable] TaskID is invalid.");
        
        shouldRun = true;
        
        while(shouldRun && taskID != -1)
        {
            try
            {
                Thread.sleep(WordleClient.THREAD_SLEEP_DURATION);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            
            WordleTask.Result taskResult = WordleClient.GetTaskResult(taskID);
            
            if(taskResult != null)
            {
                if(taskResult.getType() == WordleTask.ResultType.EXIT_LOBBY_SUCCESS)
                {
                    System.out.println("[BackToGameSelectRunnable] Exit lobby success.");
                    lobbyActivity.runOnUiThread(lobbyActivity::BackToGameSelect);
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in BackToGameSelectRunnable, exiting.");
                }
                shouldRun = false;
            }
        }
        
        System.out.println("[BackToGameSelectRunnable] End of run function.");
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
