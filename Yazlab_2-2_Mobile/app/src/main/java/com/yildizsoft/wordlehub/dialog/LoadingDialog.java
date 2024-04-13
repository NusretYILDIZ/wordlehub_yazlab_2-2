package com.yildizsoft.wordlehub.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.yildizsoft.wordlehub.R;

public class LoadingDialog
{
    private       AlertDialog dialog;
    private final Activity    activity;
    private final String message;

    public LoadingDialog(Activity activity, String message)
    {
        this.activity = activity;
        this.message = message;
    }

    public void Show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.loading_animation, null);
        TextView msgText = view.findViewById(R.id.messageText);
        msgText.setText(message);
        
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void Dismiss()
    {
        dialog.dismiss();
    }
}
