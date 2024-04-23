package com.yildizsoft.wordlehub.game.online.gameplay;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

public class ReturnToLobbyRunnable implements Runnable
{
    private final GuessWordActivity guessWordActivity;
    private static boolean shouldRun;
    
    public ReturnToLobbyRunnable(GuessWordActivity guessWordActivity)
    {
        this.guessWordActivity = guessWordActivity;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.RETURN_TO_LOBBY_AFTER_GAME, null));
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
                if(taskResult.getType() == WordleTask.ResultType.RETURNED_TO_LOBBY)
                {
                    guessWordActivity.runOnUiThread(guessWordActivity::GoToLobby);
                    Stop();
                }
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
