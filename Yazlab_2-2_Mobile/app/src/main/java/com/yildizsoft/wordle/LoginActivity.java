package com.yildizsoft.wordle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity
{
    //private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wordle_login);
        //fragmentManager = getSupportFragmentManager();

        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        EditText username = findViewById(R.id.loginUsernameInput);
        EditText password = findViewById(R.id.loginPasswordInput);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //ShowDialogBox("Username: " + username.getText().toString() + "\nPassword: " + password.getText().toString());
                WordleClient.wordleTasks.add(new WordleTask(WordleTaskType.POST, Arrays.asList(username.getText().toString(), password.getText().toString())));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //WordleClient.wordleTasks.add(new WordleTask(WordleTaskType.QUIT, ""));
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                //finish();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        WordleClient.wordleTasks.add(new WordleTask(WordleTaskType.QUIT, null));
    }

    /*@Deprecated
    public static void ShowDialogBox(String msg)
    {
        new InfoBox(msg).show(fragmentManager, "info");
    }*/
}