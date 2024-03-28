package com.yildizsoft.onlinewordle;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class SplashScreenRunnable implements Runnable
{
    private final SplashScreenActivity splashActivity;
    private final Handler handler;

    public SplashScreenRunnable(SplashScreenActivity activity, Handler handler)
    {
        this.splashActivity = activity;
        this.handler = handler;
    }

    @Override
    public void run()
    {
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.START_SERVER, null));
        while(true)
        {
            System.out.println("Inside of run.");

            if(WordleClient.taskStatus != null)
            {
                if(WordleClient.taskStatus.equals("START_SERVER_SUCCESS"))
                {
                    WordleClient.taskStatus = null;
                    //splashActivity.runOnUiThread(splashActivity::GoToLoginActivity);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    //splashActivity.GoToLoginActivity();
                    return;
                }
                else
                {
                    WordleClient.taskStatus = null;
                    Message msg = Message.obtain();
                    msg.what = 0;
                    handler.sendMessage(msg);
                    //splashActivity.runOnUiThread(splashActivity::ConnectionErrorDialog);
                    //splashActivity.ConnectionErrorDialog();
                    return;
                }
            }

            /*if(WordleClient.IsTaskResultsEmpty())
            {
            }
            else if(WordleClient.GetLastTaskResult() == WordleTaskResult.START_SERVER_SUCCESS)
            {
                WordleClient.RemoveLastTaskResult();
                splashActivity.GoToLoginActivity();
                return;
            }
            else
            {
                WordleClient.RemoveLastTaskResult();
                splashActivity.ConnectionErrorDialog();
                return;
            }*/

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
