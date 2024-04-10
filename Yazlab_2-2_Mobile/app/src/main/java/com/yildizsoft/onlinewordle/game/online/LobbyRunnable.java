package com.yildizsoft.onlinewordle.game.online;

import com.yildizsoft.onlinewordle.client.WordleClient;
import com.yildizsoft.onlinewordle.client.WordleTask;
import com.yildizsoft.onlinewordle.client.WordleTaskResult;
import com.yildizsoft.onlinewordle.client.WordleTaskType;

public class LobbyRunnable implements Runnable
{
    private final LobbyActivity lobbyActivity;
    private static boolean shouldRun = true;
    
    public LobbyRunnable(LobbyActivity lobbyActivity)
    {
        this.lobbyActivity = lobbyActivity;
    }
    
    @Override
    public void run()
    {
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.PLAYER_LIST, null));
        shouldRun = true;
        
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
                if(WordleClient.GetLastTaskResult() == WordleTaskResult.PLAYER_LIST_SUCCESS)
                {
                    WordleClient.RemoveLastTaskResult();
                    lobbyActivity.runOnUiThread(() -> lobbyActivity.CreatePlayerList(WordleClient.wordleTaskResultParam.split("\"")));
                }
                else if(WordleClient.GetLastTaskResult() == WordleTaskResult.PLAYER_LIST_FAIL_LOGIN_REQUIRED)
                {
                    WordleClient.RemoveLastTaskResult();
                    lobbyActivity.runOnUiThread(lobbyActivity::GoToLogin);
                    return;
                }
                else if(WordleClient.GetLastTaskResult() == WordleTaskResult.PLAYER_LIST_FAIL_OTHER || WordleClient.GetLastTaskResult() == WordleTaskResult.PLAYER_LIST_FAIL_NO_PLAYERS)
                {
                    WordleClient.RemoveLastTaskResult();
                    lobbyActivity.runOnUiThread(lobbyActivity::NoPlayers);
                }
                else if(WordleClient.GetLastTaskResult() == WordleTaskResult.EXIT_LOBBY_SUCCESS)
                {
                    WordleClient.RemoveLastTaskResult();
                    lobbyActivity.runOnUiThread(lobbyActivity::BackToGameSelect);
                    return;
                }
                else if(WordleClient.GetLastTaskResult() == WordleTaskResult.EXIT_LOBBY_FAIL)
                {
                    WordleClient.RemoveLastTaskResult();
                    lobbyActivity.runOnUiThread(lobbyActivity::CannotExitLobby);
                }
                else
                {
                    WordleClient.RemoveLastTaskResult();
                }
                
                //return;
                try
                {
                    Thread.sleep(1000);
                }
                catch(InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
                WordleClient.AddNewTask(new WordleTask(WordleTaskType.PLAYER_LIST, null));
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
