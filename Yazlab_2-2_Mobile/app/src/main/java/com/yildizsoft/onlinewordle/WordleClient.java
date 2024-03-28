package com.yildizsoft.onlinewordle;

import androidx.fragment.app.FragmentManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class WordleClient implements Runnable
{
    public static ArrayList<WordleTask> wordleTasks = new ArrayList<>();
    public static ArrayList<WordleTaskResult> wordleTaskResults = new ArrayList<>();
    public static boolean taskSuccessful;
    public static String taskStatus;

    private static String serverIP;
    private static int serverPort;
    private static Socket clientSock;
    private static PrintWriter clientOut;
    private static BufferedReader clientIn;
    //private boolean shouldRun;
    private static FragmentManager fragmentManager;

    public WordleClient(String ip, int port)
    {
        serverIP = ip;
        serverPort = port;
        //this.shouldRun = true;
        wordleTasks.clear();
        wordleTaskResults.clear();
        taskStatus = null;
    }

    @Override
    public void run()
    {
        //StartClient();
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
                    StopClient();
                    return;
                }
                else
                {
                    wordleTasks.get(i).setProcessing(true);
                    HandleTask(task);
                    wordleTasks.remove(i);
                }
            }
        }

        //StopClient();
    }

    public static void StartClient()
    {
        try
        {
            //clientSock = new Socket(serverIP, serverPort);
            clientSock = new Socket();
            clientSock.connect(new InetSocketAddress(serverIP, serverPort), 2000);
            clientOut = new PrintWriter(clientSock.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            //ShowDialogBox("Sunucuya başarıyla bağlanıldı.");
            System.out.println("Successfully connected to the server.");
            wordleTaskResults.add(WordleTaskResult.START_SERVER_SUCCESS);
            taskStatus = "START_SERVER_SUCCESS";
        }
        catch (IOException e)
        {
            //LoginActivity.ShowDialogBox("Sunucuya bağlanırken hata oluştu.\n" + e);
            //shouldRun = false;
            System.out.println("Failed connecting to the server.");
            wordleTaskResults.add(WordleTaskResult.START_SERVER_FAIL);
            taskStatus = "START_SERVER_FAIL";
            //ShowDialogBox("Sunucuya bağlanırken bir hata oluştu.\n\n" + e);
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

        switch(wordleTask.getTask())
        {
        case START_SERVER:
            System.out.println("Starting client...");
            StartClient();
            break;

        case SIGNUP:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            response = WaitForResponse();
            //taskSuccessful = (response != null && response.equals("SIGNUP_SUCCESS"));
            if(response != null && response.equals("SIGNUP_SUCCESS")) wordleTaskResults.add(WordleTaskResult.SIGNUP_SUCCESS);
            else wordleTaskResults.add(WordleTaskResult.SIGNUP_FAIL);
            break;

        case LOGIN:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            response = WaitForResponse();
            //taskSuccessful = (response != null && response.equals("LOGIN_SUCCESS"));
            if(response != null && response.equals("LOGIN_SUCCESS")) wordleTaskResults.add(WordleTaskResult.LOGIN_SUCCESS);
            else wordleTaskResults.add(WordleTaskResult.LOGIN_FAIL);
            break;
        }
    }

    public static void DisconnectClient()
    {
        if(clientOut != null) SendMessageToServer("quit");
        //LoginActivity.ShowDialogBox("Bağlantı kesiliyor.");
    }

    public static void StopClient()
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
            System.err.println("Client kapatılırken bir hata oluştu.\n\n" + e);
        }
        catch(NullPointerException e)
        {
            ShowDialogBox("Client kapatılırken bir hata oluştu.\n\n" + e);
            System.err.println("Client kapatılırken bir hata oluştu.\n\n" + e);
        }
    }

    public static void AddNewTask(WordleTask newTask)
    {
        wordleTasks.add(newTask);
        //HandleTask(newTask);
        //wordleTasks.remove(newTask);
    }

    public static WordleTaskResult GetLastTaskResult()
    {
        return wordleTaskResults.get(wordleTaskResults.size() - 1);
    }

    public static void RemoveLastTaskResult()
    {
        wordleTaskResults.remove(wordleTaskResults.size() - 1);
    }

    public static boolean IsTaskResultsEmpty()
    {
        return wordleTaskResults.isEmpty();
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
        //new InfoBox(msg, InfoBox.INFO).show(fragmentManager, "");
    }
}
