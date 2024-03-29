package com.yildizsoft.onlinewordle;

public class SplashScreenRunnable implements Runnable
{
    private final SplashScreenActivity splashActivity;

    public SplashScreenRunnable(SplashScreenActivity activity)
    {
        this.splashActivity = activity;
    }

    @Override
    public void run()
    {
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.START_SERVER, null));
        while(true)
        {
            System.out.println("Inside of run.");

            /*if(WordleClient.taskStatus != null)
            {
                if(WordleClient.taskStatus.equals("START_SERVER_SUCCESS"))
                {
                    WordleClient.taskStatus = null;
                    splashActivity.runOnUiThread(splashActivity::GoToLoginActivity);
                    return;
                }
                else
                {
                    WordleClient.taskStatus = null;
                    splashActivity.runOnUiThread(splashActivity::ConnectionErrorDialog);
                    return;
                }
            }*/

            if(WordleClient.IsTaskResultsEmpty())
            {
            }
            else if(WordleClient.GetLastTaskResult() == WordleTaskResult.START_SERVER_SUCCESS)
            {
                WordleClient.RemoveLastTaskResult();
                splashActivity.runOnUiThread(splashActivity::GoToLoginActivity);
                return;
            }
            else
            {
                WordleClient.RemoveLastTaskResult();
                splashActivity.runOnUiThread(splashActivity::ConnectionErrorDialog);
                return;
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
    }
}
