package com.yildizsoft.wordlehub.startup;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;
import com.yildizsoft.wordlehub.client.WordleTaskResult;
import com.yildizsoft.wordlehub.client.WordleTaskType;

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
        System.out.println("Started new ServerSelectRunnable.");
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.START_SERVER, Arrays.asList(serverIP, String.valueOf(serverPort))));

        while(shouldRun)
        {
            System.out.println("Inside of run.");

            if(!WordleClient.IsTaskResultsEmpty())
            {
                if(WordleClient.GetLastTaskResult() == WordleTaskResult.START_SERVER_SUCCESS)
                {
                    WordleClient.RemoveLastTaskResult();
                    splashActivity.runOnUiThread(splashActivity::DismissWaitDialog);
                    splashActivity.runOnUiThread(splashActivity::GoToLoginActivity);
                    shouldRun = false;
                    //return;
                }
                else
                {
                    WordleClient.RemoveLastTaskResult();
                    splashActivity.runOnUiThread(splashActivity::DismissWaitDialog);
                    splashActivity.runOnUiThread(splashActivity::ConnectionErrorDialog);
                    shouldRun = false;
                    //return;
                }
            }

            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException e)
            {
                System.err.println("SplashScreenRunnable cannot wait.");
                throw new RuntimeException(e);
            }
        }
        
        System.out.println("Exiting ServerSelectRunnable.");
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
