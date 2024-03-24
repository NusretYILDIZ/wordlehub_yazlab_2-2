package com.yildizsoft.wordle;

import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity
{
    //private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //fragmentManager = getSupportFragmentManager();
        final String[] username = new String[1];
        final String[] password = new String[1];

        Button loginButton = findViewById(R.id.loginButton);
        //Button signUpButton = findViewById(R.id.signUpButton);
        EditText usernameEditText = findViewById(R.id.loginUsernameInput);
        EditText passwordEditText = findViewById(R.id.loginPasswordInput);

        TextView emptyUsername = findViewById(R.id.emptyUsernameText);
        TextView emptyPassword = findViewById(R.id.emptyPasswordText);
        TextView signUpText = findViewById(R.id.signUpText);

        SpannableString str = new SpannableString(getString(R.string.sign_up_if_dont_have_acc));
        str.setSpan(new UnderlineSpan(), 22, str.length() - 1, 0);
        signUpText.setText(str);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean valid = true;

                if(usernameEditText.getText().toString().isEmpty())
                {
                    emptyUsername.setVisibility(View.VISIBLE);
                    valid = false;
                }
                else
                {
                    emptyUsername.setVisibility(View.GONE);
                    username[0] = usernameEditText.getText().toString();
                }

                if(passwordEditText.getText().toString().isEmpty())
                {
                    emptyPassword.setVisibility(View.VISIBLE);
                    valid = false;
                }
                else
                {
                    emptyPassword.setVisibility(View.GONE);
                    password[0] = passwordEditText.getText().toString();
                }

                if(!valid) return;

                //ShowDialogBox("Username: " + username.getText().toString() + "\nPassword: " + password.getText().toString());
                WordleClient.wordleTasks.add(new WordleTask(WordleTaskType.LOGIN, Arrays.asList(username[0], password[0])));
            }
        });

        signUpText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });
    }

    /*@Override
    protected void onDestroy()
    {
        super.onDestroy();
        WordleClient.wordleTasks.add(new WordleTask(WordleTaskType.QUIT, null));
    }*/

    /*@Deprecated
    public static void ShowDialogBox(String msg)
    {
        new InfoBox(msg).show(fragmentManager, "info");
    }*/
}