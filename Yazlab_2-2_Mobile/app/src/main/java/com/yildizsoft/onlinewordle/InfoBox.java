package com.yildizsoft.onlinewordle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import org.jetbrains.annotations.NotNull;

public class InfoBox extends DialogFragment
{
    String message;

    public InfoBox(String msg)
    {
        this.message = msg;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(this.message)
                .setNeutralButton("Tamam", null);

        return builder.create();
    }
}
