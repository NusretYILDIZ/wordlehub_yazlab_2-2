package com.yildizsoft.onlinewordle;

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
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView signUpText = findViewById(R.id.signUpText);

        SpannableString str = new SpannableString(getString(R.string.sign_up_if_dont_have_acc));
        str.setSpan(new UnderlineSpan(), 22, str.length() - 1, 0);
        signUpText.setText(str);

        Button loginButton = findViewById(R.id.loginButton);
        EditText usernameEditText = findViewById(R.id.loginUsernameInput);
        EditText passwordEditText = findViewById(R.id.loginPasswordInput);

        TextView emptyUsername = findViewById(R.id.emptyUsernameText);
        TextView emptyPassword = findViewById(R.id.emptyPasswordText);

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

                if(!valid) return;

                WordleClient.AddNewTask(new WordleTask(WordleTaskType.LOGIN, Arrays.asList(username, password)));
                //if(WordleClient.taskSuccessful) WordleClient.ShowDialogBox("Oturum başarıyla açıldı.");
                //else WordleClient.ShowDialogBox("Oturum açma başarısız oldu.");
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
}