package com.yildizsoft.wordle;

import android.app.AlertDialog;
import android.content.Context;
import androidx.fragment.app.FragmentManager;
import kotlinx.coroutines.channels.Send;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class WordleClient implements Runnable
{
    public static ArrayList<WordleTask> wordleTasks = new ArrayList<>();
    private String serverIP;
    private int serverPort;
    private static Socket clientSock;
    private static PrintWriter clientOut;
    private static BufferedReader clientIn;
    private boolean shouldRun;
    private static FragmentManager fragmentManager;

    public WordleClient(String serverIP, int serverPort)
    {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.shouldRun = true;
        wordleTasks.clear();
    }

    @Override
    public void run()
    {
        StartClient();
        //ShowDialogBox("Sunucuya başarıyla bağlanıldı.");

        while(true)
        {
            for(WordleTask task : wordleTasks)
            {
                if(task.getTask() == WordleTaskType.QUIT)
                {
                    wordleTasks.remove(task);
                    DisconnectClient();
                    return;
                }
                else
                {
                    //SendMessageToServer(task.getTask().toString());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(task.getTask().toString());

                    for(String msg : task.getContents())
                    {
                        //SendMessageToServer(msg);
                        stringBuilder.append('"').append(msg);
                    }
                    SendMessageToServer(stringBuilder.toString());
                    wordleTasks.remove(task);
                }
            }
        }

        //StopClient();
    }

    public void StartClient()
    {
        try
        {
            clientSock = new Socket(serverIP, serverPort);
            clientOut = new PrintWriter(clientSock.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            //ShowDialogBox("Sunucuya başarıyla bağlanıldı.");
        }
        catch (IOException e)
        {
            //LoginActivity.ShowDialogBox("Sunucuya bağlanırken hata oluştu.\n" + e);
            //shouldRun = false;
            ShowDialogBox("Sunucuya bağlanırken bir hata oluştu.\n\n" + e);
        }
    }

    public void SendMessageToServer(String message)
    {
        clientOut.println(message);
        //ShowDialogBox("Sunucuya mesaj gönderildi.");
    }

    public void DisconnectClient()
    {
        SendMessageToServer("quit");
        //LoginActivity.ShowDialogBox("Bağlantı kesiliyor.");
    }

    public void StopClient()
    {
        wordleTasks.clear();

        try
        {
            clientIn.close();
            clientOut.close();
            clientSock.close();
            //ShowDialogBox("Sunucu bağlantısı başarıyla sonlandırıldı.");
        }
        catch(IOException e)
        {
            //LoginActivity.ShowDialogBox("Client kapatılırken bir hata oluştu.\n" + e);
            //shouldRun = false;
            ShowDialogBox("Client kapatılırken bir hata oluştu.\n\n" + e);
        }
    }

    public static void AddNewTask(WordleTask newTask)
    {
        wordleTasks.add(newTask);
    }

    public static void SetFragmentManager(FragmentManager fm)
    {
        fragmentManager = fm;
    }

    public static void ShowDialogBox(String msg)
    {
        /*new AlertDialog.Builder(context)
                .setMessage(msg)
                .setNeutralButton("Tamam", null)
                .create()
                .show();*/
        new InfoBox(msg).show(fragmentManager, "");
    }
}
