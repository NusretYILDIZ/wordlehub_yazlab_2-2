package com.yildizsoft.wordlehub.game.online.lobby;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

public class PlayerListRunnable implements Runnable
{
    private final LobbyActivity lobbyActivity;
    private static boolean shouldRun = true;
    
    public PlayerListRunnable(LobbyActivity lobbyActivity)
    {
        this.lobbyActivity = lobbyActivity;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.PLAYER_LIST, null));
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
                if(taskResult.getType() == WordleTask.ResultType.PLAYER_LIST_SUCCESS)
                {
                    lobbyActivity.runOnUiThread(() -> lobbyActivity.CreatePlayerList(taskResult.getParameters().toArray(new String[0])));
                    taskID = RequestNewPlayerList();
                }
                else if(taskResult.getType() == WordleTask.ResultType.PLAYER_LIST_FAIL_NO_PLAYERS || taskResult.getType() == WordleTask.ResultType.PLAYER_LIST_FAIL_OTHER)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::NoPlayers);
                    taskID = RequestNewPlayerList();
                }
                else if(taskResult.getType() == WordleTask.ResultType.PLAYER_LIST_FAIL_LOGIN_REQUIRED)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::GoToLogin);
                    shouldRun = false;
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in LobbyRunnable, exiting.");
                    shouldRun = false;
                }
            }
        }
    }
    
    public long RequestNewPlayerList()
    {
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return WordleClient.AddNewTask(new WordleTask(WordleTask.Type.PLAYER_LIST, null));
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
