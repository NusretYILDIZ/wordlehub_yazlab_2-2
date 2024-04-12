package com.yildizsoft.wordlehub.signup;

import com.yildizsoft.wordlehub.client.WordleClient;
import com.yildizsoft.wordlehub.client.WordleTask;

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
        long taskID = WordleClient.AddNewTask(new WordleTask(WordleTask.Type.SIGNUP, Arrays.asList(this.username, this.password)));
        
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
                if(taskResult.getType() == WordleTask.ResultType.SIGNUP_SUCCESS)
                {
                    signUpActivity.runOnUiThread(signUpActivity::GoToLoginActivity);
                }
                else if(taskResult.getType() == WordleTask.ResultType.SIGNUP_FAIL_USER_ALREADY_EXISTS)
                {
                    signUpActivity.runOnUiThread(() -> signUpActivity.SignUpFailedDialog(SignUpFailedDialog.USER_ALREADY_EXISTS));
                }
                else if(taskResult.getType() == WordleTask.ResultType.SIGNUP_FAIL_OTHER)
                {
                    signUpActivity.runOnUiThread(() -> signUpActivity.SignUpFailedDialog(SignUpFailedDialog.OTHER));
                }
                else
                {
                    System.err.println("Unknown task result '" + taskResult + "' in SignUpRunnable, exiting.");
                }
                shouldRun = false;
            }
        }
    }
}
