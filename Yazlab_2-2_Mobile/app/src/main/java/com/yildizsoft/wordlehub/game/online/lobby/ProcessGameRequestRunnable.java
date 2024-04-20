package com.yildizsoft.wordlehub.game.online.lobby;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Collections;

public class ProcessGameRequestRunnable implements Runnable
{
    private final LobbyActivity lobbyActivity;
    private final String username;
    private final boolean accept;
    private static boolean shouldRun = true;
    
    public ProcessGameRequestRunnable(LobbyActivity lobbyActivity, String username, boolean accept)
    {
        this.lobbyActivity = lobbyActivity;
        this.username = username;
        this.accept = accept;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask((this.accept ? WordleTask.Type.ACCEPT_GAME_REQUEST : WordleTask.Type.REJECT_GAME_REQUEST), Collections.singletonList(this.username)));
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
                if(taskResult.getType() == WordleTask.ResultType.GAME_REQUEST_ACCEPTED)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::GoToGameStart);
                }
                else if(taskResult.getType() == WordleTask.ResultType.GAME_REQUEST_REJECTED)
                {
                    lobbyActivity.runOnUiThread(() -> lobbyActivity.RejectInfoDialog(username));
                }
                else System.err.println("Unknown task result '" + taskResult + "' in ProcessGameRequestRunnable, exiting.");
                Stop();
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
