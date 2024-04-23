package com.yildizsoft.wordlehub.game.online.gameplay;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Arrays;
import java.util.Collections;

public class RematchRequestRunnable implements Runnable
{
    private final GuessWordActivity guessWordActivity;
    private final String playerToRematch;
    private static boolean shouldRun;
    
    public RematchRequestRunnable(GuessWordActivity guessWordActivity, String playerToRematch)
    {
        this.guessWordActivity = guessWordActivity;
        this.playerToRematch = playerToRematch;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.OFFER_REMATCH, Collections.singletonList(this.playerToRematch)));
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
                if(taskResult.getType() == WordleTask.ResultType.REMATCH_OFFER_SENT)
                {
                    taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.WAIT_FOR_REMATCH_RESPONSE, null));
                }
                else if(taskResult.getType() == WordleTask.ResultType.REMATCH_ACCEPTED)
                {
                    guessWordActivity.runOnUiThread(guessWordActivity::RematchAccepted);
                    Stop();
                }
                else if(taskResult.getType() == WordleTask.ResultType.REMATCH_DECLINED)
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
