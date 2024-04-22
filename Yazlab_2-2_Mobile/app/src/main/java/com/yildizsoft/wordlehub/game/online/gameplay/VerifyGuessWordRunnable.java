package com.yildizsoft.wordlehub.game.online.gameplay;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Collections;

public class VerifyGuessWordRunnable implements Runnable
{
    private final GuessWordActivity guessWordActivity;
    private static boolean          shouldRun = true;
    private String word;
    
    public VerifyGuessWordRunnable(GuessWordActivity guessWordActivity, String word)
    {
        this.guessWordActivity = guessWordActivity;
        this.word              = word;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.SEND_WORD, Collections.singletonList(this.word)));
        System.out.println("[VerifyEnterWordRunnable] Task ID: " + taskID);
        
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
                if(taskResult.getType() == WordleTask.ResultType.INVALID_WORD)
                {
                    guessWordActivity.runOnUiThread(guessWordActivity::InvalidWord);
                }
                else if(taskResult.getType() == WordleTask.ResultType.VALID_WORD)
                {
                    guessWordActivity.runOnUiThread(() -> guessWordActivity.ValidWord(taskResult.getParameters()));
                }
                else System.err.println("Unknown task result \"" + taskResult + "\" in VerifyEnterWordRunnable, exiting.");
                
                Stop();
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
