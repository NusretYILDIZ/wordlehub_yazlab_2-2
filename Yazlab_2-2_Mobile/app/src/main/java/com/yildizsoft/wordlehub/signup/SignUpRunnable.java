package com.yildizsoft.wordlehub.signup;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;
import com.yildizsoft.wordlehub.client.WordleTaskResult;
import com.yildizsoft.wordlehub.client.WordleTaskType;

import java.util.Arrays;

public class SignUpRunnable implements Runnable
{
    private final SignUpActivity signUpActivity;
    private final String username;
    private final String password;
    private static boolean shouldRun;
    
    public SignUpRunnable(SignUpActivity signUpActivity, String username, String password)
    {
        this.signUpActivity = signUpActivity;
        this.username = username;
        this.password = password;
        shouldRun = true;
    }
    
    @Override
    public void run()
    {
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.SIGNUP, Arrays.asList(this.username, this.password)));
        
        while(shouldRun)
        {
            if(!WordleClient.IsTaskResultsEmpty())
            {
                shouldRun = false;
                if(WordleClient.GetLastTaskResult() == WordleTaskResult.SIGNUP_SUCCESS)
                {
                    WordleClient.RemoveLastTaskResult();
                    signUpActivity.runOnUiThread(signUpActivity::GoToLoginActivity);
                }
                else
                {
                    WordleTaskResult result = WordleClient.GetLastTaskResult();
                    WordleClient.RemoveLastTaskResult();
                    int failCode;
                    
                    if(result == WordleTaskResult.SIGNUP_FAIL_USER_ALREADY_EXISTS) failCode = SignUpFailedDialog.USER_ALREADY_EXISTS;
                    else failCode = SignUpFailedDialog.OTHER;
                    
                    signUpActivity.runOnUiThread(() -> signUpActivity.SignUpFailedDialog(failCode));
                }
                return;
            }
            
            try
            {
                Thread.sleep(500);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
