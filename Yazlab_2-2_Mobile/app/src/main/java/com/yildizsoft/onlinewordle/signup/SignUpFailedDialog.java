package com.yildizsoft.onlinewordle.signup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import org.jetbrains.annotations.NotNull;

public class SignUpFailedDialog extends DialogFragment
{
    public static final int USER_ALREADY_EXISTS = 0;
    public static final int OTHER = 1;
    
    private String failMessage;
    
    public SignUpFailedDialog(int failCode)
    {
        if(failCode == USER_ALREADY_EXISTS)
        {
            this.failMessage = "Bu kullanıcı adı başka bir oyuncu tarafından kullanılıyor. Lütfen başka bir kullanıcı adı giriniz.";
        }
        else
        {
            this.failMessage = "Hesap bilgileri kaydedilirken bir hata oluştu.";
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
