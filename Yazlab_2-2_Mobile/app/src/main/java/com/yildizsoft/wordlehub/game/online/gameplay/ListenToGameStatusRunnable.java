package com.yildizsoft.wordlehub.game.online.gameplay;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

public class ListenToGameStatusRunnable implements Runnable
{
    private final GuessWordActivity guessWordActivity;
    private static boolean shouldRun;
    
    public ListenToGameStatusRunnable(GuessWordActivity guessWordActivity)
    {
        this.guessWordActivity = guessWordActivity;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.CHECK_GAME_STATUS, null));
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
                if(taskResult.getType() == WordleTask.ResultType.GAME_CONTINUES)
                {
                    taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.CHECK_GAME_STATUS, null));
                    //Stop();
                }
                else if(taskResult.getType() == WordleTask.ResultType.GAME_OVER)
                {
                    guessWordActivity.runOnUiThread(() -> guessWordActivity.GameOver(taskResult.getParameters()));
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
