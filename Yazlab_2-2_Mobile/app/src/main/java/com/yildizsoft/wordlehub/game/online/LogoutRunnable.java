package com.yildizsoft.wordlehub.game.online;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

public class LogoutRunnable implements Runnable
{
    private final LobbyActivity lobbyActivity;
    private static boolean shouldRun = true;
    
    public LogoutRunnable(LobbyActivity lobbyActivity)
    {
        this.lobbyActivity = lobbyActivity;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.LOGOUT, null));
        
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
                if(taskResult.getType() == WordleTask.ResultType.LOGOUT_SUCCESS)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::Logout);
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in LogoutRunnable, exiting.");
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
