package com.yildizsoft.wordlehub.game.online.lobby;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

public class ListenToGameRequestsRunnable implements Runnable
{
    private final LobbyActivity lobbyActivity;
    private static boolean shouldRun = true;
    
    public ListenToGameRequestsRunnable(LobbyActivity lobbyActivity)
    {
        this.lobbyActivity = lobbyActivity;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.LISTEN_TO_GAME_REQUESTS, null));
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
            //System.out.println("[ListenToGameRequestsRunnable] TaskResult = " + taskResult);
            
            if(taskResult != null)
            {
                if(taskResult.getType() == WordleTask.ResultType.NEW_REQUEST_FOUND)
                {
                    lobbyActivity.runOnUiThread(() -> lobbyActivity.NewRequestReceivedDialog(taskResult.getParameters().get(0)));
                }
                Stop();
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
