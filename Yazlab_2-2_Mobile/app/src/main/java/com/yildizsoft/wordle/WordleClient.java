package com.yildizsoft.wordle;

import androidx.fragment.app.FragmentManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class WordleClient implements Runnable
{
    public static ArrayList<WordleTask> wordleTasks = new ArrayList<>();
    public static boolean taskSuccessful;

    private String serverIP;
    private int serverPort;
    private static Socket clientSock;
    private static PrintWriter clientOut;
    private static BufferedReader clientIn;
    //private boolean shouldRun;
    private static FragmentManager fragmentManager;

    public WordleClient(String serverIP, int serverPort)
    {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        //this.shouldRun = true;
        wordleTasks.clear();
    }

    @Override
    public void run()
    {
        StartClient();
        //ShowDialogBox("Sunucuya başarıyla bağlanıldı.");

        while(true)
        {
            for (int i = wordleTasks.size() - 1; i >= 0; i--)
            {
                WordleTask task = wordleTasks.get(i);
                if (task.getTask() == WordleTaskType.QUIT)
                {
                    wordleTasks.remove(i);
                    DisconnectClient();
                    //return;
                }
                else
                {
                    //SendMessageToServer(task.getTask().toString());
                    /*StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(task.getTask().toString());

                    for(String msg : task.getContents())
                    {
                        //SendMessageToServer(msg);
                        stringBuilder.append('"').append(msg);
                    }*/
                    //SendMessageToServer(CreateMessageFromTask(task));
                    wordleTasks.remove(i);
                    HandleTask(task);
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

    public static void SendMessageToServer(String message)
    {
        clientOut.println(message);
        ShowDialogBox("Sunucuya mesaj gönderildi.");
    }

    public static String CreateMessageFromTask(WordleTask wordleTask)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(wordleTask.getTask().toString());

        for(String s : wordleTask.getContents())
            stringBuilder.append('"').append(s);

        return stringBuilder.toString();
    }

    public static String WaitForResponse()
    {
        try
        {
            return clientIn.readLine();
        }
        catch(IOException e)
        {
            ShowDialogBox("Sunucudan cevap beklerken bir hata oluştu.\n\n" + e);
            return null;
        }
    }

    public static void HandleTask(WordleTask wordleTask)
    {
        String response;
        if (Objects.requireNonNull(wordleTask.getTask()) == WordleTaskType.SIGNUP)
        {
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            response = WaitForResponse();
            taskSuccessful = (response != null && response.equals("SIGNUP_SUCCESS"));
        }
        else if (wordleTask.getTask() == WordleTaskType.LOGIN)
        {
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            response = WaitForResponse();
            taskSuccessful = (response != null && response.equals("LOGIN_SUCCESS"));
        }
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
        //HandleTask(newTask);
        //wordleTasks.remove(newTask);
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
