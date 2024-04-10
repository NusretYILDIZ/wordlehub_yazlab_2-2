package com.yildizsoft.onlinewordle.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WordleClient implements Runnable
{
    public static ArrayList<WordleTask> wordleTasks = new ArrayList<>();
    public static ArrayList<WordleTaskResult> wordleTaskResults = new ArrayList<>();
    public static String wordleTaskResultParam = "";
    public static String taskStatus;

    //private static String serverIP;
    //private static int serverPort;
    private static Socket         clientSock;
    private static PrintWriter    clientPrinter;
    private static BufferedReader clientReader;
    private static boolean        shouldRun;
    
    private static PlayerInfo player = null;
    private static final String uid = UUID.randomUUID().toString();

    public WordleClient()
    {
        //serverIP = ip;
        //serverPort = port;
        shouldRun = true;
        wordleTasks.clear();
        wordleTaskResults.clear();
        //wordleTaskResult = null;
        taskStatus = null;
        //clientSock = new Socket();
    }
    
    /*public static void SetServerIpPort(String ip, int port)
    {
        serverIP = ip;
        serverPort = port;
    }*/

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
                else if(!task.isProcessing())
                {
                    wordleTasks.get(i).setProcessing(true);
                    HandleTask(task);
                    wordleTasks.remove(i);
                }
                break;
            }

            try
            {
                Thread.sleep(50);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public static void StartClient(String ip, int port)
    {
        System.out.println("Connecting to " + ip + ':' + port + "...");
        try
        {
            clientSock = new Socket();
            //clientSock.connect(new InetSocketAddress(serverIP, serverPort), 3000);
            clientSock.connect(new InetSocketAddress(ip, port), 3000);
            clientPrinter = new PrintWriter(clientSock.getOutputStream(), true);
            clientReader  = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            
            clientPrinter.println(uid);
            
            /*if(player != null)
            {
                clientPrinter.println(player.getId());
            }*/

            System.out.println("Successfully connected to the server.");
            AddTaskResult(WordleTaskResult.START_SERVER_SUCCESS);
            taskStatus = "START_SERVER_SUCCESS";
        }
        catch (IOException e)
        {
            System.err.println("Failed connecting to the server.\n" + e);
            AddTaskResult(WordleTaskResult.START_SERVER_FAIL);
            taskStatus = "START_SERVER_FAIL";
        }
    }

    public static void SendMessageToServer(String message)
    {
        clientPrinter.println(message);
    }

    public static String CreateMessageFromTask(WordleTask wordleTask)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(wordleTask.getTask().toString());

        if(wordleTask.getContents() != null)
            for(String s : wordleTask.getContents())
                stringBuilder.append('"').append(s);

        return stringBuilder.toString();
    }

    public static String WaitForResponse()
    {
        try
        {
            return clientReader.readLine();
        }
        catch(IOException e)
        {
            return null;
        }
    }
    
    public static void DisconnectClient()
    {
        if(clientPrinter != null) SendMessageToServer("quit");
    }
    
    public static void StopClient()
    {
        shouldRun = false;
        wordleTasks.clear();
        wordleTaskResults.clear();
        //wordleTaskResult = null;
        
        try
        {
            clientReader.close();
            clientPrinter.close();
            clientSock.close();
        }
        catch(IOException | NullPointerException e)
        {
            System.err.println("Client kapatılırken bir hata oluştu.\n" + e);
        }
    }
    
    public static void SetPlayerLobby(PlayerLobby lobby)
    {
        if(player != null) player.setLobby(lobby);
    }
    
    public static PlayerInfo GetCurrentPlayer()
    {
        return player;
    }

    public static void HandleTask(WordleTask wordleTask)
    {
        switch(wordleTask.getTask())
        {
        case START_SERVER:
            StartClient(wordleTask.getContents().get(0), Integer.parseInt(wordleTask.getContents().get(1)));
            break;

        case SIGNUP:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            SignUpTask();
            break;

        case LOGIN:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            LoginTask();
            break;
            
        case ENTER_LOBBY:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            EnterLobbyTask();
            break;
            
        case EXIT_LOBBY:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            ExitLobbyTask();
            break;
            
        case PLAYER_LIST:
            SendMessageToServer(CreateMessageFromTask(wordleTask));
            PlayerListTask();
            break;
        }
    }
    
    protected static void SignUpTask()
    {
        String response = WaitForResponse();
        System.out.println("Sign up response: " + response);
        
        if(response != null && response.equals("SIGNUP_SUCCESS")) AddTaskResult(WordleTaskResult.SIGNUP_SUCCESS);
        else
        {
            if(response == null) AddTaskResult(WordleTaskResult.SIGNUP_FAIL_OTHER);
            else if(response.equals("SIGNUP_FAIL_USER_ALREADY_EXISTS")) AddTaskResult(WordleTaskResult.SIGNUP_FAIL_USER_ALREADY_EXISTS);
            else AddTaskResult(WordleTaskResult.SIGNUP_FAIL_OTHER);
        }
    }
    
    protected static void LoginTask()
    {
        String response = WaitForResponse();
        System.out.println("Login response: " + response);
        taskStatus = response;
        
        if(response != null && response.startsWith("LOGIN_SUCCESS"))
        {
            String[] splitResponse = response.split("\"");
            String id = splitResponse[1];
            String username = splitResponse[2];
            
            player = new PlayerInfo(id, username);
            AddTaskResult(WordleTaskResult.LOGIN_SUCCESS);
        }
        else
        {
            if(response == null) AddTaskResult(WordleTaskResult.LOGIN_FAIL_OTHER);
            
            else if(response.equals("LOGIN_FAIL_USERNAME_NOT_FOUND"))
                AddTaskResult(WordleTaskResult.LOGIN_FAIL_USERNAME_NOT_FOUND);
            
            else if(response.equals("LOGIN_FAIL_WRONG_PASSWORD"))
                AddTaskResult(WordleTaskResult.LOGIN_FAIL_WRONG_PASSWORD);
            
            else AddTaskResult(WordleTaskResult.LOGIN_FAIL_OTHER);
        }
    }
    
    protected static void EnterLobbyTask()
    {
        String response = WaitForResponse();
        System.out.println("Enter lobby response: " + response);
        
        if(response != null && response.startsWith("ENTER_LOBBY_SUCCESS"))
            AddTaskResult(WordleTaskResult.ENTER_LOBBY_SUCCESS);
        
        else AddTaskResult(WordleTaskResult.ENTER_LOBBY_FAIL);
    }
    
    protected static void ExitLobbyTask()
    {
        String response = WaitForResponse();
        System.out.println("Exit lobby response: " + response);
        
        if(response != null && response.startsWith("EXIT_LOBBY_SUCCESS"))
            AddTaskResult(WordleTaskResult.EXIT_LOBBY_SUCCESS);
        
        else AddTaskResult(WordleTaskResult.EXIT_LOBBY_FAIL);
    }
    
    protected static void PlayerListTask()
    {
        String response = WaitForResponse();
        System.out.println("Player list response: " + response);
        
        if(response == null) AddTaskResult(WordleTaskResult.PLAYER_LIST_FAIL_OTHER);
        
        else if(response.equals("LOGIN_REQUIRED")) AddTaskResult(WordleTaskResult.PLAYER_LIST_FAIL_LOGIN_REQUIRED);
        
        else if(response.equals("NO_PLAYERS")) AddTaskResult(WordleTaskResult.PLAYER_LIST_FAIL_NO_PLAYERS);
        
        else
        {
            System.out.println("Oyuncu listesi alındı.");
            // TODO: Add a task result that contains the player list.
            List<String> tokens = new ArrayList<>(Arrays.asList(response.split("\"")));
            tokens.remove(0);
            
            StringBuilder builder = new StringBuilder(tokens.get(0));
            for(int i = 1; i < tokens.size(); i++) builder.append('"').append(tokens.get(i));
            
            wordleTaskResultParam = builder.toString();
            AddTaskResult(WordleTaskResult.PLAYER_LIST_SUCCESS);
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
