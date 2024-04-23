package com.yildizsoft.wordlehub.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

public class WordleClient implements Runnable
{
    public static final int THREAD_SLEEP_DURATION = 100;
    
    public static ArrayList<WordleTask>            wordleTasks           = new ArrayList<>();
    private static long taskCounter = 0;
    private static Socket         clientSock;
    private static PrintWriter    clientPrinter;
    private static BufferedReader clientReader;
    private static boolean        shouldRun;
    
    private static       PlayerInfo player    = null;
    private static final String    uid       = UUID.randomUUID().toString();
    private static final Semaphore taskMutex = new Semaphore(1);
    private static List<String> serverResponses = new ArrayList<>();
    
    public WordleClient()
    {
        shouldRun = true;
        FlushTaskList();
    }
    
    @Override
    public void run()
    {
        while(shouldRun)
        {
            try
            {
                Thread.sleep(THREAD_SLEEP_DURATION);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }
            
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
            
            serverResponses.clear();
            serverResponses = GetServerResponses();
            /*String line;
            while((line = WaitForResponse()) != null)
                serverResponses.add(line);*/
            
            for(int i = tasks.size() - 1; i >= 0; i--)
            {
                WordleTask task = tasks.get(i);

                if(task.getStatus() != WordleTask.Status.COMPLETED)
                {
                    //tasks.get(i).setStatus(WordleTask.Status.IN_PROGRESS);
                    //SetTaskStatus(task, WordleTask.Status.IN_PROGRESS);
                    HandleTask(task);
                    //SetTaskStatus(task, WordleTask.Status.COMPLETED);
                    //tasks.get(i).setStatus(WordleTask.Status.DONE);
                    //wordleTasks.remove(i);
                }
                //break;
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
    
    public static List<String> GetServerResponses()
    {
        List<String> responses = new ArrayList<>();
        
        if(clientReader != null)
        {
            try
            {
                while(clientReader.ready())
                {
                    String line = clientReader.readLine();
                    if(line != null) responses.add(line);
                }
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        
        return responses;
    }
    
    public static String WaitForResponse()
    {
        //if(clientReader != null)
        //{
            try
            {
                String line = clientReader.readLine();
                line = line.replaceAll("test", "");
                return line;
                //return clientReader.readLine();
            }
            catch(IOException e)
            {
                return null;
            }
        //}
        //return null;
    }
    
    public static String GetResponseOf(WordleTask.Type taskType)
    {
        //List<String> responses = new ArrayList<>();
        
        for(String response : serverResponses)
        {
            switch(taskType)
            {
            case SIGNUP:
                if(response.contains("SIGNUP_SUCCESS") ||
                   response.contains("SIGNUP_FAIL_USER_ALREADY_EXISTS") ||
                   response.contains("SIGNUP_FAIL_OTHER"))
                    return response;
                //responses.add(response);
                break;
            
            case LOGIN:
                if(response.contains("LOGIN_SUCCESS") ||
                   response.contains("LOGIN_FAIL_USERNAME_NOT_FOUND") ||
                   response.contains("LOGIN_FAIL_USER_ALREADY_LOGGED_IN") ||
                   response.contains("LOGIN_FAIL_WRONG_PASSWORD") ||
                   response.contains("LOGIN_FAIL_OTHER"))
                    return response;
                //responses.add(response);
                break;
            
            case LOGOUT:
                if(response.contains("LOGOUT_SUCCESS") ||
                   response.contains("LOGOUT_FAIL"))
                    return response;
                //responses.add(response);
                break;
            
            case ENTER_LOBBY:
                if(response.contains("ENTER_LOBBY_SUCCESS") ||
                   response.contains("ENTER_LOBBY_FAIL"))
                    return response;
                //responses.add(response);
                break;
            
            case EXIT_LOBBY:
                if(response.contains("EXIT_LOBBY_SUCCESS") ||
                   response.contains("EXIT_LOBBY_FAIL"))
                    return response;
                //responses.add(response);
                break;
            
            case PLAYER_LIST:
                if(response.contains("PLAYERS_IN_LOBBY") ||
                   response.contains("NO_PLAYERS") ||
                   response.contains("LOGIN_REQUIRED") ||
                   response.contains("LOGIN_OTHER"))
                    return response;
                //responses.add(response);
                break;
            
            case SEND_GAME_REQUEST:
                if(response.contains("SEND_GAME_REQUEST_SUCCESS") ||
                   response.contains("SEND_GAME_REQUEST_ALREADY_REQUESTED") ||
                   response.contains("SEND_GAME_REQUEST_NO_LONGER_ONLINE") ||
                   response.contains("SEND_GAME_REQUEST_FAIL_OTHER"))
                    return response;
                //responses.add(response);
                break;
            
            case WAIT_GAME_REQUEST_RESPONSE:
                if(response.contains("GAME_REQUEST_ACCEPTED") ||
                   response.contains("GAME_REQUEST_REJECTED"))
                    return response;
                //responses.add(response);
                break;
            
            case LISTEN_TO_GAME_REQUESTS:
                if(response.contains("NEW_REQUEST"))
                    return response;
                //responses.add(response);
                break;
            
            case ACCEPT_GAME_REQUEST:
                if(response.contains("GAME_REQUEST_ACCEPTED"))
                    return response;
                //responses.add(response);
                break;
                
            case REJECT_GAME_REQUEST:
                if(response.contains("GAME_REQUEST_REJECTED"))
                    return response;
                //responses.add(response);
                break;
            
            case PRE_GAME_SEND_WORD:
            case IN_GAME_SEND_WORD:
                if(response.contains("INVALID_WORD") ||
                   response.contains("VALID_WORD"))
                    return response;
                //responses.add(response);
                break;
            
            case LISTEN_TO_ENTER_WORD_TIMER:
                if(response.contains("START_GAME"))
                    return response;
                //responses.add(response);
                break;
            
            case CHECK_GAME_STATUS:
                if(response.contains("GAME_CONTINUES") ||
                   response.contains("GAME_OVER"))
                    return response;
                //responses.add(response);
                break;
                
            case OFFER_REMATCH:
                if(response.contains("REMATCH_OFFER_SENT"))
                    return response;
                break;
                
            case WAIT_FOR_REMATCH_RESPONSE:
                if(response.contains("REMATCH_ACCEPTED") ||
                   response.contains("REMATCH_DECLINED"))
                    return response;
                break;
                
            case LISTEN_TO_REMATCH_OFFERS:
                if(response.contains("NEW_REMATCH_OFFER"))
                    return response;
                break;
                
            case ACCEPT_REMATCH:
                if(response.contains("REMATCH_ACCEPTED"))
                    return response;
                break;
                
            case DECLINE_REMATCH:
                if(response.contains("REMATCH_DECLINED"))
                    return response;
                break;
                
            case RETURN_TO_LOBBY_AFTER_GAME:
                if(response.contains("RETURNED_TO_LOBBY"))
                    return response;
                break;
            }
        }
        
        return null;
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
        System.out.println("Wordle task: " + CreateMessageFromTask(wordleTask));
        
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
            
        case SEND_GAME_REQUEST:
            SendGameRequestTask(wordleTask);
            break;
            
        case WAIT_GAME_REQUEST_RESPONSE:
            WaitGameRequestResponseTask(wordleTask);
            break;
            
        case LISTEN_TO_GAME_REQUESTS:
            ListenToGameRequestsTask(wordleTask);
            break;
            
        case ACCEPT_GAME_REQUEST:
        case REJECT_GAME_REQUEST:
            ProcessGameRequestTask(wordleTask);
            break;
            
        case PRE_GAME_SEND_WORD:
        case IN_GAME_SEND_WORD:
            SendWordTask(wordleTask);
            break;
            
        case LISTEN_TO_ENTER_WORD_TIMER:
            ListenToEnterWordTimer(wordleTask);
            break;
            
        case CHECK_GAME_STATUS:
            CheckGameStatusTask(wordleTask);
            break;
            
        case OFFER_REMATCH:
            OfferRematchTask(wordleTask);
            break;
            
        case WAIT_FOR_REMATCH_RESPONSE:
            WaitForRematchResponseTask(wordleTask);
            break;
            
        case LISTEN_TO_REMATCH_OFFERS:
            ListenToRematchOffersTask(wordleTask);
            break;
            
        case ACCEPT_REMATCH:
            AcceptRematchTask(wordleTask);
            break;
            
        case DECLINE_REMATCH:
            DeclineRematchTask(wordleTask);
            break;
            
        case RETURN_TO_LOBBY_AFTER_GAME:
            ReturnToLobbyAfterGameTask(wordleTask);
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
        
        SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
    }
    
    protected static void DisconnectTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        
        StopClient();
        
        SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
    }
    
    protected static void SignUpTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Sign up response: " + response);
        
        if(response != null)
        {
            if(response.equals("SIGNUP_SUCCESS"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.SIGNUP_SUCCESS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.equals("SIGNUP_FAIL_USER_ALREADY_EXISTS"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.SIGNUP_FAIL_USER_ALREADY_EXISTS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.startsWith("SIGNUP_FAIL_OTHER"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.SIGNUP_FAIL_OTHER, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
        
    }
    
    protected static void LoginTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Login response: " + response);
        //taskStatus = response;
        
        
        if(response != null)
        {
            if(response.startsWith("LOGIN_SUCCESS"))
            {
                String[] splitResponse = response.split("\"");
                String   id            = splitResponse[1];
                String   username      = splitResponse[2];
                
                player = new PlayerInfo(id, username);
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_SUCCESS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            else if(response.equals("LOGIN_FAIL_USERNAME_NOT_FOUND"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_USERNAME_NOT_FOUND, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.equals("LOGIN_FAIL_WRONG_PASSWORD"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_WRONG_PASSWORD, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.equals("LOGIN_FAIL_USER_ALREADY_LOGGED_IN"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_USER_ALREADY_LOGGED_IN, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            else if(response.startsWith("LOGIN_FAIL_OTHER"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGIN_FAIL_OTHER, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
        
    }
    
    protected static void LogoutTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Logout response: " + response);
        
        if(response != null)
        {
            if(response.startsWith("LOGOUT_SUCCESS"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGOUT_SUCCESS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            else if(response.startsWith("LOGOUT_FAIL"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.LOGOUT_FAIL, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
        
    }
    
    protected static void EnterLobbyTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Enter lobby response: " + response);
        
        if(response != null)
        {
            if(response.startsWith("ENTER_LOBBY_SUCCESS"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.ENTER_LOBBY_SUCCESS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            else if(response.startsWith("ENTER_LOBBY_FAIL"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.ENTER_LOBBY_FAIL, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
        
    }
    
    protected static void ExitLobbyTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Exit lobby response: " + response);
        
        if(response != null)
        {
            if(response.startsWith("EXIT_LOBBY_SUCCESS"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.EXIT_LOBBY_SUCCESS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            else if(response.startsWith("EXIT_LOBBY_FAIL"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.EXIT_LOBBY_FAIL, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
        
    }
    
    protected static void PlayerListTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Player list response: " + response);
        
        if(response != null)
        {
            if(response.equals("LOGIN_REQUIRED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_FAIL_LOGIN_REQUIRED, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.equals("NO_PLAYERS"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_FAIL_NO_PLAYERS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.equals("LOGIN_OTHER"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_FAIL_OTHER, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.startsWith("PLAYERS_IN_LOBBY"))
            {
                System.out.println("Oyuncu listesi alındı.");
                List<String> tokens = new ArrayList<>(Arrays.asList(response.split("\"")));
                tokens.remove(0);
                
                SetTaskResult(wordleTask, WordleTask.ResultType.PLAYER_LIST_SUCCESS, tokens);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    protected static void SendGameRequestTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Send game request response: " + response);
        
        if(response != null)
        {
            if(response.startsWith("SEND_GAME_REQUEST_ALREADY_REQUESTED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.SEND_GAME_REQUEST_FAIL_ALREADY_REQUESTED, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.startsWith("SEND_GAME_REQUEST_NO_LONGER_ONLINE"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.SEND_GAME_REQUEST_FAIL_NO_LONGER_ONLINE, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.startsWith("SEND_GAME_REQUEST_FAIL_OTHER"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.SEND_GAME_REQUEST_FAIL_OTHER, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.startsWith("SEND_GAME_REQUEST_SUCCESS"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.SEND_GAME_REQUEST_SUCCESS, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    public static void WaitGameRequestResponseTask(WordleTask wordleTask)
    {
        /*try
        {
            if(clientReader.ready())
            {*/
                String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
                System.out.println("Wait game request response response: " + response);
                
                if(response != null && response.startsWith("GAME_REQUEST_ACCEPTED"))
                {
                    SetTaskResult(wordleTask, WordleTask.ResultType.GAME_REQUEST_ACCEPTED, null);
                    SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
                }
                
                else if(response != null && response.startsWith("GAME_REQUEST_REJECTED"))
                {
                    SetTaskResult(wordleTask, WordleTask.ResultType.GAME_REQUEST_REJECTED, null);
                    SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
                }
                
                //else System.err.println("Unhandled edge case in WaitGameRequestResponseTask, response: " + response);
                
         /*   }
        }
        catch(IOException e)
        {
            System.err.println("An error has occurred while waiting game request response.\n" + e);
        }*/
    }
    
    public static void ListenToGameRequestsTask(WordleTask wordleTask)
    {
        /*try
        {
            if(clientReader.ready())
            {*/
                String response = GetResponseOf(wordleTask.getTask()); //clientReader.readLine();
                System.out.println("Listen to game requests response: " + response);
                
                String[] tokens = null;
                
                if(response != null) tokens = response.split("\"");
                
                if(tokens != null && tokens[0].startsWith("NEW_REQUEST"))
                {
                    SetTaskResult(wordleTask, WordleTask.ResultType.NEW_REQUEST_FOUND, Collections.singletonList(tokens[1]));
                    SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
                }
          /*  }
        }
        catch(IOException e)
        {
            System.err.println("An error has occurred while listening to game requests.\n" + e);
        }*/
    }
    
    public static void ProcessGameRequestTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Process game request response: " + response);
        
        String[] tokens = null;
        if(response != null) tokens = response.split("\"");
        else System.err.println("[ProcessGameRequestTask] Response is null.");
        
        if(tokens != null)
        {
            if(tokens[0].startsWith("GAME_REQUEST_ACCEPTED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.GAME_REQUEST_ACCEPTED, Collections.singletonList(tokens[1]));
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(tokens[0].startsWith("GAME_REQUEST_REJECTED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.GAME_REQUEST_REJECTED, Collections.singletonList(tokens[1]));
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    public static void SendWordTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Send word task response: " + response);
        
        if(response != null)
        {
            if(response.startsWith("INVALID_WORD"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.INVALID_WORD, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.equals("VALID_WORD"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.VALID_WORD, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            
            else if(response.startsWith("VALID_WORD"))
            {
                List<String> tokens = new ArrayList<>(Arrays.asList(response.split("\"")));
                tokens.remove(0);
                
                SetTaskResult(wordleTask, WordleTask.ResultType.VALID_WORD, tokens);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    public static void ListenToEnterWordTimer(WordleTask wordleTask)
    {
        /*try
        {
            if(clientReader.ready())
            {*/
                String response = GetResponseOf(wordleTask.getTask()); //clientReader.readLine();
                System.out.println("Listen to enter word timer response: " + response);
                
                if(response != null)
                {
                    if(response.startsWith("START_GAME"))
                    {
                        SetTaskResult(wordleTask, WordleTask.ResultType.GAME_STARTS, null);
                        SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
                    }
                }
          /*  }
        }
        catch(IOException e)
        {
            System.err.println("An error has occurred while listening to game server.\n" + e);
        }*/
    }
    
    public static void CheckGameStatusTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Check game status response: " + response);
        
        if(response != null)
        {
            if(response.startsWith("GAME_OVER"))
            {
                List<String> tokens = new ArrayList<>(Arrays.asList(response.split("\"")));
                tokens.remove(0);
                
                SetTaskResult(wordleTask, WordleTask.ResultType.GAME_OVER, tokens);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
                
            }
            else if(response.equals("GAME_CONTINUES"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.GAME_CONTINUES, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
        
    }
    
    public static void OfferRematchTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Offer rematch response: " + response);
        
        if(response != null)
        {
            if(response.contains("REMATCH_OFFER_SENT"))
            {
                List<String> tokens = new ArrayList<>(Arrays.asList(response.split("\"")));
                tokens.remove(0);
                
                SetTaskResult(wordleTask, WordleTask.ResultType.REMATCH_OFFER_SENT, tokens);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            /*else if(response.contains("REMATCH_DECLINED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.REMATCH_DECLINED, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }*/
        }
    }
    
    public static void WaitForRematchResponseTask(WordleTask wordleTask)
    {
        String response = GetResponseOf(wordleTask.getTask());
        System.out.println("Wait for rematch response response: " + response);
        
        if(response != null)
        {
            if(response.contains("REMATCH_ACCEPTED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.REMATCH_ACCEPTED, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            else if(response.contains("REMATCH_DECLINED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.REMATCH_DECLINED, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    public static void ListenToRematchOffersTask(WordleTask wordleTask)
    {
        String response = GetResponseOf(wordleTask.getTask());
        System.out.println("Listen to rematch offers response: " + response);
        
        if(response != null)
        {
            if(response.contains("NEW_REMATCH_OFFER"))
            {
                List<String> tokens = new ArrayList<>(Arrays.asList(response.split("\"")));
                tokens.remove(0);
                
                SetTaskResult(wordleTask, WordleTask.ResultType.NEW_REMATCH_OFFER, tokens);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
            else if(response.contains("RETURNED_TO_LOBBY"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.RETURNED_TO_LOBBY, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    public static void AcceptRematchTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Accept rematch response: " + response);
        
        if(response != null)
        {
            if(response.contains("REMATCH_ACCEPTED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.REMATCH_ACCEPTED, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    public static void DeclineRematchTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Decline rematch response: " + response);
        
        if(response != null)
        {
            if(response.contains("REMATCH_DECLINED"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.REMATCH_DECLINED, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
        }
    }
    
    public static void ReturnToLobbyAfterGameTask(WordleTask wordleTask)
    {
        if(wordleTask.getStatus() != WordleTask.Status.IN_PROGRESS) SendMessageToServer(CreateMessageFromTask(wordleTask));
        SetTaskStatus(wordleTask, WordleTask.Status.IN_PROGRESS);
        String response = GetResponseOf(wordleTask.getTask()); //WaitForResponse();
        System.out.println("Return to lobby after match response: " + response);
        
        if(response != null)
        {
            if(response.contains("RETURNED_TO_LOBBY"))
            {
                SetTaskResult(wordleTask, WordleTask.ResultType.RETURNED_TO_LOBBY, null);
                SetTaskStatus(wordleTask, WordleTask.Status.COMPLETED);
            }
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
