package com.yildizsoft.wordle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class MainWordleActivity extends AppCompatActivity
{
    public static ArrayList<WordleTask> wordleTasks = new ArrayList<>();
    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wordle_login);
        fragmentManager = getSupportFragmentManager();

        Button loginButton = findViewById(R.id.loginButton);
        Button signUpButton = findViewById(R.id.signUpButton);
        EditText username = findViewById(R.id.loginUsernameInput);
        EditText password = findViewById(R.id.loginPasswordInput);

        WordleClient wordleClient = new WordleClient("192.168.0.103", 65535);

        Thread wordleTread = new Thread(wordleClient);
        wordleTread.start();

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ShowDialogBox("Username: " + username.getText() + "\nPassword: " + password.getText());
                wordleTasks.add(new WordleTask(WordleTaskType.POST, "Username: " + username.getText() + "\nPassword: " + password.getText()));
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordleTasks.add(new WordleTask(WordleTaskType.QUIT, ""));
            }
        });
    }

    public static void ShowDialogBox(String msg)
    {
        new InfoBox(msg).show(fragmentManager, "info");
    }
}