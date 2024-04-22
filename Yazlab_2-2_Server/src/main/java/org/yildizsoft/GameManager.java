package org.yildizsoft;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;

public class GameManager implements Runnable
{
    private PlayerInfo player1, player2;
    private PlayerGameData playerData1, playerData2;
    private        int        wordLength;
    private        boolean    inGame    = false;
    private static boolean    shouldRun = true;
    private static List<Task> tasks     = new ArrayList<>();
    private static Semaphore  taskMutex = new Semaphore(1);
    
    ScheduledExecutorService scheduledExecutorService;
    ScheduledFuture<?>       scheduledFuture;
    
    public GameManager(PlayerInfo player1, PlayerInfo player2, int wordLength)
    {
        this.player1     = player1;
        this.player2     = player2;
        this.wordLength  = wordLength;
        this.playerData1 = new PlayerGameData();
        this.playerData2 = new PlayerGameData();
        tasks.clear();
    }
    
    @Override
    public void run()
    {
        //ClientTask.AddNewTask(new ClientTask(this.player1.getId(), "START_GAME"));
        //ClientTask.AddNewTask(new ClientTask(this.player2.getId(), "START_GAME"));
        
        shouldRun = true;
        
        while(shouldRun)
        {
            /*try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException(e);
            }*/
            
            List<Task> copyOfTasks = GetTaskList();
            
            if(copyOfTasks == null)
            {
                System.err.println("[GameManager] Could not copy task list.");
                Stop();
                return;
            }
            
            for(int i = copyOfTasks.size() - 1; i >= 0; i--)
            {
                Task task = copyOfTasks.get(i);
                
                if(task.getClientID().equals(this.player1.getId()) || task.getClientID().equals(this.player2.getId()))
                {
                    RemoveTask(i);
                    HandleTask(task);
                }
            }
        }
    }
    
    public void HandleTask(Task task)
    {
        switch(task.getTask())
        {
        case "SEND_WORD":
            if(inGame) InGameSendWordTask(task);
            else PreGameSendWordTask(task);
            break;
        }
    }
    
    public void PreGameSendWordTask(Task task)
    {
        String initialWord = task.getTokens().getFirst();
        
        if(Wordstation.Exists(initialWord, this.wordLength))
        {
            System.out.println("[GameManager] We have a valid word.");
            if(task.getClientID().equals(player1.getId()))
            {
                ClientTask.AddNewTask(new ClientTask(player1.getId(), "VALID_WORD"));
                playerData1.setWordForOpponent(initialWord);
                playerData2.setWordToGuess(initialWord);
            }
            else if(task.getClientID().equals(player2.getId()))
            {
                ClientTask.AddNewTask(new ClientTask(player2.getId(), "VALID_WORD"));
                playerData2.setWordForOpponent(initialWord);
                playerData1.setWordToGuess(initialWord);
            }
            else System.err.println("[GameManager] We have an insider!");
            
            if(playerData1.getWordToGuess() != null && playerData2.getWordToGuess() != null)
            {
                inGame = true;
                ClientTask.AddNewTask(new ClientTask(player1.getId(), "START_GAME"));
                ClientTask.AddNewTask(new ClientTask(player2.getId(), "START_GAME"));
            }
        }
        else
        {
            System.out.println("[GameManager] We have an invalid word.");
            if(task.getClientID().equals(player1.getId()))
            {
                ClientTask.AddNewTask(new ClientTask(player1.getId(), "INVALID_WORD"));
            }
            else if(task.getClientID().equals(player2.getId()))
            {
                ClientTask.AddNewTask(new ClientTask(player2.getId(), "INVALID_WORD"));
            }
            else System.err.println("[GameManager] We have an insider!");
        }
    }
    
    public void InGameSendWordTask(Task task)
    {
        String initialWord = task.getTokens().getFirst();
        
        if(Wordstation.Exists(initialWord, this.wordLength))
        {
            System.out.println("[GameManager] We have a valid word.");
            if(task.getClientID().equals(player1.getId()))
            {
                List<Character> comparison = Wordstation.CompareWords(playerData1.getWordToGuess(), initialWord);
                
                StringBuilder builder = new StringBuilder("VALID_WORD");
                for(Character c : comparison) builder.append('"').append(c);
                
                ClientTask.AddNewTask(new ClientTask(player1.getId(), builder.toString()));
            }
            else if(task.getClientID().equals(player2.getId()))
            {
                //ClientTask.AddNewTask(new ClientTask(player2.getId(), "VALID_WORD"));
                List<Character> comparison = Wordstation.CompareWords(playerData2.getWordToGuess(), initialWord);
                
                StringBuilder builder = new StringBuilder("VALID_WORD");
                for(Character c : comparison) builder.append('"').append(c);
                
                ClientTask.AddNewTask(new ClientTask(player2.getId(), builder.toString()));
            }
            else System.err.println("[GameManager] We have an insider!");
        }
        else
        {
            System.out.println("[GameManager] We have an invalid word.");
            if(task.getClientID().equals(player1.getId()))
            {
                ClientTask.AddNewTask(new ClientTask(player1.getId(), "INVALID_WORD"));
            }
            else if(task.getClientID().equals(player2.getId()))
            {
                ClientTask.AddNewTask(new ClientTask(player2.getId(), "INVALID_WORD"));
            }
            else System.err.println("[GameManager] We have an insider!");
        }
    }
    
    public List<Task> GetTaskList()
    {
        List<Task> tasksCopy;
        
        try
        {
            taskMutex.acquire();
            tasksCopy = new ArrayList<>(tasks);
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("GameManager.GetTaskList function has been interrupted.\n" + e);
            tasksCopy = null;
        }
        
        return tasksCopy;
    }
    
    public static void AddTask(Task task)
    {
        try
        {
            taskMutex.acquire();
            tasks.add(task);
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("GameManager.AddTask function has been interrupted.\n" + e);
        }
    }
    
    public void RemoveTask(int i)
    {
        try
        {
            taskMutex.acquire();
            tasks.remove(i);
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("GameManager.RemoveTask function has been interrupted.\n" + e);
        }
    }
    
    public static void Stop()
    {
        shouldRun = false;
    }
    
    public static class Task
    {
        private String       clientID;
        private String       task;
        private List<String> tokens;
        
        public Task(String clientID, String task, List<String> tokens)
        {
            this.clientID = clientID;
            this.task     = task;
            this.tokens   = tokens;
        }
        
        public String getClientID()
        {
            return clientID;
        }
        
        public void setClientID(String clientID)
        {
            this.clientID = clientID;
        }
        
        public String getTask()
        {
            return task;
        }
        
        public void setTask(String task)
        {
            this.task = task;
        }
        
        public List<String> getTokens()
        {
            return tokens;
        }
        
        public void setTokens(List<String> tokens)
        {
            this.tokens = tokens;
        }
    }
    
    public static class PlayerGameData
    {
        private String       wordForOpponent;
        private String       wordToGuess;
        private List<String> guesses;
        
        public PlayerGameData()
        {
            this.wordToGuess     = null;
            this.wordForOpponent = null;
            this.guesses         = new ArrayList<>();
        }
        
        public String getWordForOpponent()
        {
            return wordForOpponent;
        }
        
        public void setWordForOpponent(String wordForOpponent)
        {
            this.wordForOpponent = wordForOpponent;
        }
        
        public String getWordToGuess()
        {
            return wordToGuess;
        }
        
        public void setWordToGuess(String wordToGuess)
        {
            this.wordToGuess = wordToGuess;
        }
        
        public List<String> getGuesses()
        {
            return guesses;
        }
        
        public void setGuesses(List<String> guesses)
        {
            this.guesses = guesses;
        }
    }
}