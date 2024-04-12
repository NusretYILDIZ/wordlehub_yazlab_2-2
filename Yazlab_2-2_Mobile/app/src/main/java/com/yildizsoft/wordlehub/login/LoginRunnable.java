package com.yildizsoft.wordlehub.login;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

import java.util.Arrays;

public class LoginRunnable implements Runnable
{
    private final  LoginActivity loginActivity;
    private final  String        username;
    private final  String        password;
    private static boolean       shouldRun;
    
    public LoginRunnable(LoginActivity loginActivity, String username, String password)
    {
        this.loginActivity = loginActivity;
        this.username      = username;
        this.password      = password;
        shouldRun          = true;
    }
    
    @Override
    public void run()
    {
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.LOGIN, Arrays.asList(username, password)));
        
        shouldRun = true;
        
        while(shouldRun && taskID != -1)
        {
            System.out.println("LoginRunnable loop.");
            
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
                if(taskResult.getType() == WordleTask.ResultType.LOGIN_SUCCESS)
                {
                    loginActivity.runOnUiThread(loginActivity::GoToGameSelection);
                }
                else if(taskResult.getType() == WordleTask.ResultType.LOGIN_FAIL_USERNAME_NOT_FOUND)
                {
                    loginActivity.runOnUiThread(() -> loginActivity.LoginFailedDialog(LoginFailedDialog.USERNAME_NOT_FOUND));
                }
                else if(taskResult.getType() == WordleTask.ResultType.LOGIN_FAIL_WRONG_PASSWORD)
                {
                    loginActivity.runOnUiThread(() -> loginActivity.LoginFailedDialog(LoginFailedDialog.WRONG_PASSWORD));
                }
                else if(taskResult.getType() == WordleTask.ResultType.LOGIN_FAIL_OTHER)
                {
                    loginActivity.runOnUiThread(() -> loginActivity.LoginFailedDialog(LoginFailedDialog.OTHER));
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in LoginRunnable, exiting.");
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
