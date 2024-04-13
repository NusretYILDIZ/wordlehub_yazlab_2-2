package com.yildizsoft.wordlehub.game.online;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Collections;

public class SendGameRequestRunnable implements Runnable
{
    private final LobbyActivity lobbyActivity;
    private final String destinationUsername;
    private static boolean shouldRun;
    
    public SendGameRequestRunnable(LobbyActivity lobbyActivity, String destinationUsername)
    {
        this.lobbyActivity = lobbyActivity;
        this.destinationUsername = destinationUsername;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.SEND_GAME_REQUEST, Collections.singletonList(this.destinationUsername)));
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
                if(taskResult.getType() == WordleTask.ResultType.SEND_GAME_REQUEST_SUCCESS)
                {
                    lobbyActivity.runOnUiThread(() -> lobbyActivity.PendingRequestDialog(this.destinationUsername));
                    taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.WAIT_GAME_REQUEST_RESPONSE, null));
                }
                else if(taskResult.getType() == WordleTask.ResultType.SEND_GAME_REQUEST_FAIL_ALREADY_REQUESTED)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::AlreadyRequestedDialog);
                }
                else if(taskResult.getType() == WordleTask.ResultType.SEND_GAME_REQUEST_FAIL_NO_LONGER_ONLINE)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::PlayerNoLongerOnlineDialog);
                }
                else if(taskResult.getType() == WordleTask.ResultType.GAME_REQUEST_ACCEPTED)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::DismissPendingRequestDialog);
                }
                else if(taskResult.getType() == WordleTask.ResultType.GAME_REQUEST_REJECTED)
                {
                    lobbyActivity.runOnUiThread(lobbyActivity::DismissPendingRequestDialog);
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in SendGameRequestRunnable, exiting.");
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
