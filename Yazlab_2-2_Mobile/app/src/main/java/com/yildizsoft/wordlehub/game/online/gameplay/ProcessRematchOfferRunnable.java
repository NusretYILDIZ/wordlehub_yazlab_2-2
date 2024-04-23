package com.yildizsoft.wordlehub.game.online.gameplay;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Collections;

public class ProcessRematchOfferRunnable implements Runnable
{
    private final GuessWordActivity guessWordActivity;
    private final boolean accept;
    private static boolean shouldRun = true;
    
    public ProcessRematchOfferRunnable(GuessWordActivity guessWordActivity, boolean accept)
    {
        this.guessWordActivity = guessWordActivity;
        this.accept            = accept;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask((this.accept ? WordleTask.Type.ACCEPT_REMATCH : WordleTask.Type.DECLINE_REMATCH), null));
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
                if(taskResult.getType() == WordleTask.ResultType.REMATCH_ACCEPTED)
                {
                    guessWordActivity.runOnUiThread(guessWordActivity::RematchAccepted);
                }
                else if(taskResult.getType() == WordleTask.ResultType.REMATCH_DECLINED)
                {
                    guessWordActivity.runOnUiThread(guessWordActivity::GoToLobbyRequest);
                }
                else System.err.println("Unknown task result '" + taskResult + "' in ProcessRematchOfferRunnable, exiting.");
                Stop();
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
