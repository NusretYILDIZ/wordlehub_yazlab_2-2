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

public class SignUpActivity extends AppCompatActivity
{
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        TextView login = findViewById(R.id.loginText);

        SpannableString str = new SpannableString(getString(R.string.login_if_has_account));
        str.setSpan(new UnderlineSpan(), 25, str.length() - 1, 0);
        login.setText(str);

        WordleClient.SetFragmentManager(getSupportFragmentManager());

        //final String[] username = new String[1];
        //final String[] password = new String[1];
        //final String[] passwordConfirm = new String[1];

        EditText usernameEditText = findViewById(R.id.loginUsernameInput);
        EditText passwordEditText = findViewById(R.id.loginPasswordInput);
        EditText passwordConfirmEditText = findViewById(R.id.loginPasswordConfirmInput);

        TextView emptyUsername = findViewById(R.id.emptyUsernameText);
        TextView emptyPassword = findViewById(R.id.emptyPasswordText);
        TextView passwordMatch = findViewById(R.id.passwordMatchText);

        Button signUp = findViewById(R.id.signUpButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToLoginActivity();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener()
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
                    username = usernameEditText.getText().toString();
                }

                if(passwordEditText.getText().toString().isEmpty())
                {
                    emptyPassword.setVisibility(View.VISIBLE);
                    valid = false;
                }
                else
                {
                    emptyPassword.setVisibility(View.GONE);
                    password = passwordEditText.getText().toString();
                }

                if(passwordConfirmEditText.getText().toString().isEmpty() || (password != null && !password.equals(passwordConfirmEditText.getText().toString())))
                {
                    passwordMatch.setVisibility(View.VISIBLE);
                    valid = false;
                }
                else
                {
                    passwordMatch.setVisibility(View.GONE);
                    //passwordConfirm[0] = passwordConfirmEditText.getText().toString();
                }

                if(!valid) return;

                WordleClient.ShowDialogBox(username + "\n" + password);
                WordleClient.AddNewTask(new WordleTask(WordleTaskType.SIGNUP, Arrays.asList(username, password)));
                GoToLoginActivity();
            }
        });
    }

    protected void GoToLoginActivity()
    {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    /*@Override
    protected void onDestroy()
    {
        super.onDestroy();
        WordleClient.AddNewTask(new WordleTask(WordleTaskType.QUIT, null));
    }*/
}