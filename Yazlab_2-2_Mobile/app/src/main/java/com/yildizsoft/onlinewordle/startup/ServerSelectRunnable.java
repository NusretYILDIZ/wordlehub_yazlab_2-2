package com.yildizsoft.onlinewordle.startup;

import com.yildizsoft.onlinewordle.client.WordleClient;
import com.yildizsoft.onlinewordle.client.WordleTask;
import com.yildizsoft.onlinewordle.client.WordleTaskResult;
import com.yildizsoft.onlinewordle.client.WordleTaskType;

public class ServerSelectRunnable implements Runnable
{
    private final ServerSelectActivity splashActivity;
    private static boolean shouldRun;

    public ServerSelectRunnable(ServerSelectActivity activity)
    {
        this.splashActivity = activity;
        shouldRun = true;
    }

    @Override
    public void run()
    {
        System.out.println("Started new ServerSelectRunnable.");
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.START_SERVER, null));

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
