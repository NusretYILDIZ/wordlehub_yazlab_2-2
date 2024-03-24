package com.yildizsoft.wordle;

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

        while(shouldRun)
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
                    SendMessageToServer(task.getTask().toString());
                    for(String msg : task.getContents())
                    {
                        SendMessageToServer(msg);
                    }
                    wordleTasks.remove(task);
                }
            }
        }

        StopClient();
    }

    public void StartClient()
    {
        try
        {
            clientSock = new Socket(serverIP, serverPort);
            clientOut = new PrintWriter(clientSock.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
        }
        catch (IOException e)
        {
            //LoginActivity.ShowDialogBox("Sunucuya bağlanırken hata oluştu.\n" + e);
            shouldRun = false;
        }
    }

    public void SendMessageToServer(String message)
    {
        clientOut.println(message);
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
        }
        catch(IOException e)
        {
            //LoginActivity.ShowDialogBox("Client kapatılırken bir hata oluştu.\n" + e);
            shouldRun = false;
        }
    }

    public static void AddNewTask(WordleTask newTask)
    {
        wordleTasks.add(newTask);
    }
}
