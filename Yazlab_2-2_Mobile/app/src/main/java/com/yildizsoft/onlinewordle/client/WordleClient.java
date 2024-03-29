package com.yildizsoft.onlinewordle.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class WordleClient implements Runnable
{
    public static ArrayList<WordleTask> wordleTasks = new ArrayList<>();
    public static ArrayList<WordleTaskResult> wordleTaskResults = new ArrayList<>();
    //public static WordleTaskResult wordleTaskResult;
    public static boolean taskSuccessful;
    public static String taskStatus;

    private static String serverIP;
    private static int serverPort;
    private static Socket clientSock;
    private static PrintWriter clientOut;
    private static BufferedReader clientIn;
    private static boolean shouldRun;

    public WordleClient(String ip, int port)
    {
        serverIP = ip;
        serverPort = port;
        shouldRun = true;
        wordleTasks.clear();
        wordleTaskResults.clear();
        //wordleTaskResult = null;
        taskStatus = null;
    }

    @Override
    public void run()
    {
        while(shouldRun)
        {
            for (int i = wordleTasks.size() - 1; i >= 0; i--)
            {
                WordleTask task = wordleTasks.get(i);
                if (task.getTask() == WordleTaskType.QUIT)
                {
                    wordleTasks.remove(i);
                    DisconnectClient();
                    StopClient();
                    shouldRun = false;
                }
                else //if(!task.isProcessing())
                {
                    wordleTasks.get(i).setProcessing(true);
                    HandleTask(task);
                    wordleTasks.remove(i);
                }
                break;
            }

            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void StartClient()
    {
        try
        {
            clientSock = new Socket();
            clientSock.connect(new InetSocketAddress(serverIP, serverPort), 2000);
            clientOut = new PrintWriter(clientSock.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));

            System.out.println("Successfully connected to the server.");
            AddTaskResult(WordleTaskResult.START_SERVER_SUCCESS);
            taskStatus = "START_SERVER_SUCCESS";
        }
        catch (IOException e)
        {
            System.out.println("Failed connecting to the server.");
            AddTaskResult(WordleTaskResult.START_SERVER_FAIL);
            taskStatus = "START_SERVER_FAIL";
        }
    }

    public static void SendMessageToServer(String message)
    {
        clientOut.println(message);
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
            if(response != null && response.equals("SIGNUP_SUCCESS")) AddTaskResult(WordleTaskResult.SIGNUP_SUCCESS);
            else
            {
                if(response == null) AddTaskResult(WordleTaskResult.SIGNUP_FAIL_OTHER);
                else if(response.equals("SIGNUP_FAIL_USER_ALREADY_EXISTS")) AddTaskResult(WordleTaskResult.SIGNUP_FAIL_USER_ALREADY_EXISTS);
                else AddTaskResult(WordleTaskResult.SIGNUP_FAIL_OTHER);
            }
            break;

        case LOGIN:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            response = WaitForResponse();
            System.out.println("Response: " + response);
            
            if(response != null && response.equals("LOGIN_SUCCESS"))
            {
                taskStatus = response;
                AddTaskResult(WordleTaskResult.LOGIN_SUCCESS);
            }
            else
            {
                taskStatus = response;
                if(response == null) AddTaskResult(WordleTaskResult.LOGIN_FAIL_OTHER);
                
                else if(response.equals("LOGIN_FAIL_USERNAME_NOT_FOUND"))
                    AddTaskResult(WordleTaskResult.LOGIN_FAIL_USERNAME_NOT_FOUND);
                
                else if(response.equals("LOGIN_FAIL_WRONG_PASSWORD"))
                    AddTaskResult(WordleTaskResult.LOGIN_FAIL_WRONG_PASSWORD);
                
                else AddTaskResult(WordleTaskResult.LOGIN_FAIL_OTHER);
            }
            break;
        }
    }

    public static void DisconnectClient()
    {
        if(clientOut != null) SendMessageToServer("quit");
    }

    public static void StopClient()
    {
        shouldRun = false;
        wordleTasks.clear();
        wordleTaskResults.clear();
        //wordleTaskResult = null;

        try
        {
            clientIn.close();
            clientOut.close();
            clientSock.close();
        }
        catch(IOException | NullPointerException e)
        {
            System.err.println("Client kapatılırken bir hata oluştu.\n" + e);
        }
    }

    public static void AddNewTask(WordleTask newTask)
    {
        wordleTasks.add(newTask);
    }
    
    public static void AddTaskResult(WordleTaskResult result)
    {
        //wordleTaskResult = result;
        wordleTaskResults.add(result);
    }

    public static WordleTaskResult GetLastTaskResult()
    {
        return wordleTaskResults.get(wordleTaskResults.size() - 1);
        //return wordleTaskResult;
    }

    public static void RemoveLastTaskResult()
    {
        wordleTaskResults.remove(wordleTaskResults.size() - 1);
        //wordleTaskResult = null;
    }

    public static boolean IsTaskResultsEmpty()
    {
        return wordleTaskResults.isEmpty();
        //return wordleTaskResult == null;
    }
}
