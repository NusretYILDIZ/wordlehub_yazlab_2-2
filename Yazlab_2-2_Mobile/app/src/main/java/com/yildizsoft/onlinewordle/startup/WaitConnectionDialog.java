package com.yildizsoft.onlinewordle.startup;

import android.app.Activity;
import android.app.AlertDialog;
import com.yildizsoft.onlinewordle.R;

public class WaitConnectionDialog
{
    private       AlertDialog dialog;
    private final Activity    activity;

    public WaitConnectionDialog(Activity activity)
    {
        this.activity = activity;
    }

    public void Show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.loading_animation);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void Dismiss()
    {
        dialog.dismiss();
    }
}
