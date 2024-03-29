package com.yildizsoft.onlinewordle.login;

import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.yildizsoft.onlinewordle.R;
import com.yildizsoft.onlinewordle.signup.SignUpActivity;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity
{
    protected LoginActivity loginActivity;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        loginActivity = this;
        
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
                
                new Thread(new LoginRunnable(loginActivity, username, password)).start();
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
    
    public void LoginFailedDialog(int failCode)
    {
        new LoginFailedDialog(failCode).show(getSupportFragmentManager(), "loginFail");
    }
    
    @Override
    protected void onDestroy()
    {
        LoginRunnable.Stop();
        super.onDestroy();
    }
}