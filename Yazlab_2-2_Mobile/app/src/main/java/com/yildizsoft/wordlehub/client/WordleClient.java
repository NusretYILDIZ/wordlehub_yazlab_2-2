package com.yildizsoft.wordlehub.client;

import com.yildizsoft.wordlehub.game.online.LogoutRunnable;

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
import java.util.concurrent.Semaphore;

public class WordleClient implements Runnable
{
    public static final int THREAD_SLEEP_DURATION = 100;
    
    public static ArrayList<WordleTask>            wordleTasks           = new ArrayList<>();
    private static long taskCounter = 0;
    
    //public static ArrayList<WordleTask.ResultType> wordleTaskResultTypes = new ArrayList<>();
    //public static String                           wordleTaskResultParam = "";
    //public static String                           taskStatus;
    
    //private static String serverIP;
    //private static int serverPort;
    private static Socket         clientSock;
    private static PrintWriter    clientPrinter;
    private static BufferedReader clientReader;
    private static boolean        shouldRun;
    
    private static       PlayerInfo player    = null;
    private static final String    uid       = UUID.randomUUID().toString();
    private static final Semaphore taskMutex = new Semaphore(1);
    
    public WordleClient()
    {
        //serverIP = ip;
        //serverPort = port;
        shouldRun = true;
        FlushTaskList();
        //wordleTasks.clear();
        //wordleTaskResultTypes.clear();
        //wordleTaskResult = null;
        //taskStatus = null;
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
            List<WordleTask> tasks = null;
            
            try
            {
                taskMutex.acquire();
                tasks = new ArrayList<>(wordleTasks);
            }
            catch(InterruptedException e)
            {
                System.err.println("WordleClient runnable has been interrupted.\n" + e);
            }
            finally
            {
                taskMutex.release();
            }
            
            if(tasks == null)
            {
                System.err.println("Task list could not be copied.");
                return;
            }
            
            for(int i = tasks.size() - 1; i >= 0; i--)
            {
                WordleTask task = tasks.get(i);

                if(task.getStatus() == WordleTask.Status.WAITING)
                {
                    //tasks.get(i).setStatus(WordleTask.Status.IN_PROGRESS);
                    SetTaskStatus(task, WordleTask.Status.IN_PROGRESS);
                    HandleTask(task);
                    SetTaskStatus(task, WordleTask.Status.COMPLETED);
                    //tasks.get(i).setStatus(WordleTask.Status.DONE);
                    //wordleTasks.remove(i);
                }
                //break;
            }
            
            try
            {
                Thread.sleep(THREAD_SLEEP_DURATION);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static boolean StartClient(String ip, int port)
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
            return true;
            //AddTaskResult(WordleTask.ResultType.START_SERVER_SUCCESS);
            //taskStatus = "START_SERVER_SUCCESS";
        }
        catch(IOException e)
        {
            System.err.println("Failed connecting to the server.\n" + e);
            return false;
            //AddTaskResult(WordleTask.ResultType.START_SERVER_FAIL);
            //taskStatus = "START_SERVER_FAIL";
        }
    }
    
    public static void SendMessageToServer(String message)
    {
        if(clientPrinter != null) clientPrinter.println(message);
    }
    
    public static String CreateMessageFromTask(WordleTask wordleTask)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(wordleTask.getTask().toString());
        
        if(wordleTask.getParameters() != null)
            for(String s : wordleTask.getParameters())
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
        SendMessageToServer("quit");
    }
    
    public static void StopClient()
    {
        FlushTaskList();
        //shouldRun = false;
        //wordleTasks.clear();
        //wordleTaskResultTypes.clear();
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
        case QUIT:
            DisconnectTask(wordleTask);
            break;
        
        case CONNECT_TO_SERVER:
            ConnectToServerTask(wordleTask);
            break;
        
        case SIGNUP:
            SignUpTask(wordleTask);
            break;
        
        case LOGIN:
            LoginTask(wordleTask);
            break;
        
        case LOGOUT:
            LogoutTask(wordleTask);
            break;
        
        case ENTER_LOBBY:
            EnterLobbyTask(wordleTask);
            break;
        
        case EXIT_LOBBY:
            ExitLobbyTask(wordleTask);
            break;
        
        case PLAYER_LIST:
            PlayerListTask(wordleTask);
            break;
        }
    }
    
    protected static void ConnectToServerTask(WordleTask wordleTask)
    {
        String serverIP   = wordleTask.getParameters().get(0);
        int    serverPort = Integer.parseInt(wordleTask.getParameters().get(1));
        
        boolean result = StartClient(serverIP, serverPort);
        
        if(result) SetTaskResult(wordleTask, WordleTask.ResultType.CONNECT_TO_SERVER_SUCCESS, null);
        else SetTaskResult(wordleTask, WordleTask.ResultType.CONNECT_TO_SERVER_FAIL, null);
    }
    
    protected static void DisconnectTask(WordleTask wordleTask)
    {
        SendMessageToServer(CreateMessageFromTask(wordleTask));
        StopClient();
    }
    
    protected static void SignUpTask(WordleTask wordleTask)
    {
        SendMessageToServer(CreateMessageFromTask(wordleTask));
        String response = WaitForResponse();
        System.out.println("Sign up response: " + response);
        
        if(response != null && response.equals("SIGNUP_SUCCESS"))
            SetTaskResult(wordleTask, WordleTask.ResultType.SIGNUP_SUCCESS, null);
            //AddTaskResult(WordleTask.ResultType.SIGNUP_SUCCESS);
        
        else
        {
            if(response == null)
                SetTaskResult(wordleTask, WordleTask.ResultType.SIGNUP_FAIL_OTHER, null);
                //AddTaskResult(WordleTask.ResultType.SIGNUP_FAIL_OTHER);
            
            else if(response.equals("SIGNUP_FAIL_USER_ALREADY_EXISTS"))
                SetTaskResult(wordleTask, WordleTask.ResultType.SIGNUP_FAIL_USER_ALREADY_EXISTS, null);
                //AddTaskResult(WordleTask.ResultType.SIGNUP_FAIL_USER_ALREADY_EXISTS);
            
            else
                SetTaskResult(wordleTask, WordleTask.ResultType.SIGNUP_FAIL_OTHER, null);
            //AddTaskResult(WordleTask.ResultType.SIGNUP_FAIL_OTHER);
        }
    }
    
    protected static void LoginTask(WordleTask wordleTask)
    {
        SendMessageToServer(CreateMessageFromTask(wordleTask));
        String response = WaitForResponse();
        System.out.println("Login response: " + response);
        //taskStatus = response;
        
        if(response != null && response.startsWith("LOGIN_SUCCESS"))
        {
            String[] splitResponse = response.split("\"");
            String   id            = splitResponse[1];
            String   username      = splitResponse[2];
            
            player = new PlayerInfo(id, username);
            SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_SUCCESS, null);
            //AddTaskResult(WordleTask.ResultType.LOGIN_SUCCESS);
        }
        else
        {
            if(response == null) SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_OTHER, null); //AddTaskResult(WordleTask.ResultType.LOGIN_FAIL_OTHER);
            
            else if(response.equals("LOGIN_FAIL_USERNAME_NOT_FOUND"))
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_USERNAME_NOT_FOUND, null);
                //AddTaskResult(WordleTask.ResultType.LOGIN_FAIL_USERNAME_NOT_FOUND);
            
            else if(response.equals("LOGIN_FAIL_WRONG_PASSWORD"))
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_WRONG_PASSWORD, null);
                //AddTaskResult(WordleTask.ResultType.LOGIN_FAIL_WRONG_PASSWORD);
            
            else SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_OTHER, null); //AddTaskResult(WordleTask.ResultType.LOGIN_FAIL_OTHER);
        }
    }
    
    protected static void LogoutTask(WordleTask wordleTask)
    {
        SendMessageToServer(CreateMessageFromTask(wordleTask));
        String response = WaitForResponse();
        System.out.println("Logout response: " + response);
        
        if(response != null && response.startsWith("LOGOUT_SUCCESS"))
            SetTaskResult(wordleTask, WordleTask.ResultType.LOGOUT_SUCCESS, null);
        
        else SetTaskResult(wordleTask, WordleTask.ResultType.LOGOUT_FAIL, null);
    }
    
    protected static void EnterLobbyTask(WordleTask wordleTask)
    {
        SendMessageToServer(CreateMessageFromTask(wordleTask));
        String response = WaitForResponse();
        System.out.println("Enter lobby response: " + response);
        
        if(response != null && response.startsWith("ENTER_LOBBY_SUCCESS"))
            SetTaskResult(wordleTask, WordleTask.ResultType.ENTER_LOBBY_SUCCESS, null);
            //AddTaskResult(WordleTask.ResultType.ENTER_LOBBY_SUCCESS);
        
        else SetTaskResult(wordleTask, WordleTask.ResultType.ENTER_LOBBY_FAIL, null); //AddTaskResult(WordleTask.ResultType.ENTER_LOBBY_FAIL);
    }
    
    protected static void ExitLobbyTask(WordleTask wordleTask)
    {
        SendMessageToServer(CreateMessageFromTask(wordleTask));
        String response = WaitForResponse();
        System.out.println("Exit lobby response: " + response);
        
        if(response != null && response.startsWith("EXIT_LOBBY_SUCCESS"))
            SetTaskResult(wordleTask, WordleTask.ResultType.EXIT_LOBBY_SUCCESS, null);
            //AddTaskResult(WordleTask.ResultType.EXIT_LOBBY_SUCCESS);
        
        else SetTaskResult(wordleTask, WordleTask.ResultType.EXIT_LOBBY_FAIL, null); //AddTaskResult(WordleTask.ResultType.EXIT_LOBBY_FAIL);
    }
    
    protected static void PlayerListTask(WordleTask wordleTask)
    {
        SendMessageToServer(CreateMessageFromTask(wordleTask));
        String response = WaitForResponse();
        System.out.println("Player list response: " + response);
        
        if(response == null)
            SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_FAIL_OTHER, null);
            //AddTaskResult(WordleTask.ResultType.PLAYER_LIST_FAIL_OTHER);
        
        else if(response.equals("LOGIN_REQUIRED"))
            SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_FAIL_LOGIN_REQUIRED, null);
            //AddTaskResult(WordleTask.ResultType.PLAYER_LIST_FAIL_LOGIN_REQUIRED);
        
        else if(response.equals("NO_PLAYERS"))
            SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_FAIL_NO_PLAYERS, null);
            //AddTaskResult(WordleTask.ResultType.PLAYER_LIST_FAIL_NO_PLAYERS);
        
        else
        {
            System.out.println("Oyuncu listesi alındı.");
            List<String> tokens = new ArrayList<>(Arrays.asList(response.split("\"")));
            tokens.remove(0);
            
            SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_SUCCESS, tokens);
        }
    }
    
    public static void RemoveCompletedTasks()
    {
        try
        {
            taskMutex.acquire();
            
            for(int i = wordleTasks.size() - 1; i >= 0; i--)
            {
                if(wordleTasks.get(i).getStatus() == WordleTask.Status.COMPLETED) wordleTasks.remove(i);
            }
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("TaskListCleanup function has been interrupted.\n" + e);
        }
    }
    
    public static void FlushTaskList()
    {
        try
        {
            taskMutex.acquire();
            wordleTasks.clear();
            taskCounter = 0;
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("FlushTaskList function has been interrupted.\n" + e);
        }
    }
    
    public static long AddNewTask(WordleTask newTask)
    {
        newTask.setId(taskCounter);
        
        try
        {
            taskMutex.acquire();
            wordleTasks.add(newTask);
            taskCounter++;
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("AddNewTask function has been interrupted.\n" + e);
            newTask.setId(-1);
        }
        
        return newTask.getId();
    }
    
    public static void SetTaskStatus(WordleTask task, WordleTask.Status status)
    {
        try
        {
            taskMutex.acquire();
            //wordleTasks.get(wordleTasks.indexOf(task)).setStatus(status);
            for(int i = wordleTasks.size() - 1; i >= 0; i--)
            {
                if(wordleTasks.get(i).getId() == task.getId())
                {
                    wordleTasks.get(i).setStatus(status);
                    break;
                }
            }
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("SetTaskStatus function has been interrupted.\n" + e);
        }
    }
    
    public static void SetTaskResult(WordleTask task, WordleTask.ResultType resultType, List<String> resultParameters)
    {
        try
        {
            taskMutex.acquire();
            //wordleTasks.get(wordleTasks.indexOf(task)).setResult(new WordleTask.Result(resultType, resultParameters));
            for(int i = wordleTasks.size() - 1; i >= 0; i--)
            {
                if(wordleTasks.get(i).getId() == task.getId())
                {
                    wordleTasks.get(i).setResult(new WordleTask.Result(resultType, resultParameters));
                    break;
                }
            }
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("SetTaskResult function has been interrupted.\n" + e);
        }
    }
    
    public static WordleTask.Result GetTaskResult(long taskID)
    {
        WordleTask.Result result = null;
        
        try
        {
            taskMutex.acquire();
            for(WordleTask task : wordleTasks)
            {
                if(task.getId() == taskID && task.getStatus() == WordleTask.Status.COMPLETED)
                {
                    result = task.getResult();
                    break;
                }
            }
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("GetTaskResult function has been interrupted.\n" + e);
        }
        
        return result;
    }
}
