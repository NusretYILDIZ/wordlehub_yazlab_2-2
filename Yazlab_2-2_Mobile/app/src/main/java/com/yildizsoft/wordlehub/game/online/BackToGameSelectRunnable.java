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
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.EXIT_LOBBY, null));
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
                    lobbyActivity.runOnUiThread(lobbyActivity::BackToGameSelect);
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in BackToGameSelectRunnable, exiting.");
                }
                shouldRun = false;
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
