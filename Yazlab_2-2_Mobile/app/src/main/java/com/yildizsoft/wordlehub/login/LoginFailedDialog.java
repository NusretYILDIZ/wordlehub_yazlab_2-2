package com.yildizsoft.wordlehub.login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import org.jetbrains.annotations.NotNull;

public class LoginFailedDialog extends DialogFragment
{
    public static final int USERNAME_NOT_FOUND = 0;
    public static final int WRONG_PASSWORD = 1;
    public static final int OTHER = 2;
    
    private final String failMessage;
    
    public LoginFailedDialog(int failMessage)
    {
        switch(failMessage)
        {
        case USERNAME_NOT_FOUND:
            this.failMessage = "Kullanıcı adı hatalı.";
            break;
            
        case WRONG_PASSWORD:
            this.failMessage = "Şifre hatalı.";
            break;
            
        default:
            this.failMessage = "Giriş yapmaya çalışırken bir hata meydana geldi.";
            break;
        }
    }
    
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.failMessage);
        builder.setNeutralButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        
        setCancelable(false);
        return builder.create();
    }
}
