package com.yildizsoft.onlinewordle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import org.jetbrains.annotations.NotNull;

public class InfoBox extends DialogFragment
{
    public static final int INFO = 0;
    public static final int START_CLIENT_ERROR = 1;
    public static final int LOGIN_USERNAME_NOT_FOUND = 2;
    public static final int LOGIN_PASSWORD_NOT_CORRECT = 3;

    String message;
    int boxType;

    public InfoBox(String msg, int type)
    {
        this.message = msg;
        this.boxType = type;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(this.message);
        builder.setNeutralButton("Tamam", null);

        /*switch(boxType)
        {
        case INFO:
            builder.setNeutralButton("Tamam", null);
            break;

        case START_CLIENT_ERROR:
            builder.setPositiveButton("Tekrar Dene", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            });
            break;
        }
               */

        return builder.create();
    }
}
