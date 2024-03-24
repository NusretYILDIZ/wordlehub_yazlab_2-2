package com.yildizsoft.wordle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WordleClient implements Runnable
{
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
    }

    @Override
    public void run()
    {
        StartClient();

        while(shouldRun)
        {
            for(WordleTask task : MainWordleActivity.wordleTasks)
            {
                if(task.getTaskType() == WordleTaskType.QUIT)
                {
                    MainWordleActivity.wordleTasks.remove(task);
                    SendMessageToServer(".");
                    MainWordleActivity.ShowDialogBox("Bağlantı kesiliyor.");
                    return;
                }
                else if(task.getTaskType() == WordleTaskType.POST)
                {
                    SendMessageToServer(task.getTask());
                    MainWordleActivity.wordleTasks.remove(task);
                }
            }
        }

        StopClient();
    }

    public void StartClient()
    {
        try {
            clientSock = new Socket(serverIP, serverPort);
            clientOut = new PrintWriter(clientSock.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
        } catch (IOException e) {
            MainWordleActivity.ShowDialogBox("Sunucuya bağlanırken hata oluştu.\n" + e);
            shouldRun = false;
        }
    }

    public void SendMessageToServer(String message)
    {
        clientOut.println(message);
    }

    public void StopClient()
    {
        try {
            clientIn.close();
            clientOut.close();
            clientSock.close();
        }
        catch(IOException e)
        {
            MainWordleActivity.ShowDialogBox("Client kapatılırken bir hata oluştu.\n" + e);
            shouldRun = false;
        }
    }
}
