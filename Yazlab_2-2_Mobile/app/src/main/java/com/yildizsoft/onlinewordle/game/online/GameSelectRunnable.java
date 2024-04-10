package com.yildizsoft.onlinewordle.game.online;

import com.yildizsoft.onlinewordle.client.WordleClient;
import com.yildizsoft.onlinewordle.client.WordleTask;
import com.yildizsoft.onlinewordle.client.WordleTaskResult;
import com.yildizsoft.onlinewordle.client.WordleTaskType;

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
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.ENTER_LOBBY, Arrays.asList(constLetterMode, letterCount)));
        
        while(shouldRun)
        {
            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            
            if(!WordleClient.IsTaskResultsEmpty())
            {
                if(WordleClient.GetLastTaskResult() == WordleTaskResult.ENTER_LOBBY_SUCCESS)
                    gameSelectActivity.runOnUiThread(gameSelectActivity::GoToLobby);
                
                else gameSelectActivity.runOnUiThread(gameSelectActivity::GoToLogin);
                
                WordleClient.RemoveLastTaskResult();
                return;
            }
        }
    }
}
