package com.yildizsoft.wordlehub.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class InfoDialog
{
    private final Activity activity;
    private final String message;
    
    public InfoDialog(Activity activity, String message)
    {
        this.activity = activity;
        this.message = message;
    }
    
    public void Show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        builder.setMessage(this.message);
        builder.setNeutralButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        
        builder.create().show();
    }
}
