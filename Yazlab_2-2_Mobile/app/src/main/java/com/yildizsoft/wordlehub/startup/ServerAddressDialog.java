package com.yildizsoft.wordlehub.startup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.yildizsoft.wordlehub.R;
import org.jetbrains.annotations.NotNull;

public class ServerAddressDialog extends DialogFragment
{
    private final ServerSelectActivity serverSelectActivity;
    
    public ServerAddressDialog(ServerSelectActivity serverSelectActivity)
    {
        this.serverSelectActivity = serverSelectActivity;
    }
    
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sunucu bilgilerini kontrol et");
        
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View addressView = inflater.inflate(R.layout.server_address_dialog, null);
        builder.setView(addressView);
        
        builder.setPositiveButton("Bağlan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                EditText ip = addressView.findViewById(R.id.serverIpInput);
                EditText port = addressView.findViewById(R.id.serverPortInput);
                
                int portInt = Integer.parseInt(port.getText().toString());
                
                if(portInt >= 0 && portInt <= 65535 && !ip.getText().toString().isEmpty()) serverSelectActivity.ConnectToTheServer(ip.getText().toString(), portInt);
                dialog.dismiss();
            }
        });
        
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        
        return builder.create();
    }
}
