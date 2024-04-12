package com.yildizsoft.wordlehub.game.online;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Arrays;

public class GameSelectRunnable implements Runnable
{
    private final GameSelectActivity gameSelectActivity;
    private final String constLetterMode;
    private final String letterCount;
    private static boolean shouldRun;
    
    public GameSelectRunnable(GameSelectActivity gameSelectActivity, String constLetterMode, String letterCount)
    {
        this.gameSelectActivity = gameSelectActivity;
        this.constLetterMode = constLetterMode;
        this.letterCount = letterCount;
        shouldRun = true;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.ENTER_LOBBY, Arrays.asList(constLetterMode, letterCount)));
        
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
                if(taskResult.getType() == WordleTask.ResultType.ENTER_LOBBY_SUCCESS)
                {
                    System.out.println("Enter lobby successful.");
                    gameSelectActivity.runOnUiThread(gameSelectActivity::GoToLobby);
                }
                else if(taskResult.getType() == WordleTask.ResultType.ENTER_LOBBY_FAIL)
                {
                    gameSelectActivity.runOnUiThread(gameSelectActivity::GoToLogin);
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in GameSelectRunnable, exiting.");
                }
                shouldRun = false;
            }
        }
    }
}
