package com.yildizsoft.wordlehub.startup;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Arrays;

public class ServerSelectRunnable implements Runnable
{
    private final ServerSelectActivity splashActivity;
    private static boolean shouldRun;
    
    private static String serverIP;
    private static int serverPort;

    public ServerSelectRunnable(ServerSelectActivity activity, String ip, int port)
    {
        this.splashActivity = activity;
        shouldRun = true;
        serverIP = ip;
        serverPort = port;
    }

    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.CONNECT_TO_SERVER, Arrays.asList(serverIP, String.valueOf(serverPort))));

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
                if(taskResult.getType() == WordleTask.ResultType.CONNECT_TO_SERVER_SUCCESS)
                {
                    splashActivity.runOnUiThread(splashActivity::DismissWaitDialog);
                    splashActivity.runOnUiThread(splashActivity::GoToLoginActivity);
                }
                else if(taskResult.getType() == WordleTask.ResultType.CONNECT_TO_SERVER_FAIL)
                {
                    splashActivity.runOnUiThread(splashActivity::DismissWaitDialog);
                    splashActivity.runOnUiThread(splashActivity::ConnectionErrorDialog);
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in ServerSelectRunnable, exiting.");
                }
                shouldRun = false;
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
