package com.yildizsoft.wordlehub.login;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;
import com.yildizsoft.wordlehub.client.WordleTaskResult;
import com.yildizsoft.wordlehub.client.WordleTaskType;

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
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.LOGIN, Arrays.asList(username, password)));
        
        while(shouldRun)
        {
            System.out.println("LoginRunnable loop.");
            
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
                if(WordleClient.GetLastTaskResult() == WordleTaskResult.LOGIN_SUCCESS)
                {
                    WordleClient.RemoveLastTaskResult();
                    loginActivity.runOnUiThread(loginActivity::GoToGameSelection);
                }
                else
                {
                    WordleTaskResult result = WordleClient.GetLastTaskResult();
                    WordleClient.RemoveLastTaskResult();
                    int failCode;
                    
                    if(result == WordleTaskResult.LOGIN_FAIL_USERNAME_NOT_FOUND)
                        failCode = LoginFailedDialog.USERNAME_NOT_FOUND;
                    
                    else if(result == WordleTaskResult.LOGIN_FAIL_WRONG_PASSWORD)
                        failCode = LoginFailedDialog.WRONG_PASSWORD;
                    
                    else failCode = LoginFailedDialog.OTHER;
                    
                    loginActivity.runOnUiThread(() -> loginActivity.LoginFailedDialog(failCode));
                }
                return;
            }
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
}
