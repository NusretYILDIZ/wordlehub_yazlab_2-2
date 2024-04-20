package com.yildizsoft.wordlehub.game.online.gameplay;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

public class EnterWordRunnable implements Runnable
{
    private final EnterWordActivity enterWordActivity;
    private static boolean shouldRun = true;
    
    public EnterWordRunnable(EnterWordActivity enterWordActivity)
    {
        this.enterWordActivity = enterWordActivity;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.LISTEN_TO_ENTER_WORD_TIMER, null));
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
                if(taskResult.getType() == WordleTask.ResultType.DRAW)
                {
                
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
