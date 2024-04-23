package org.yildizsoft;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ClientTask
{
    public static List<ClientTask> clientTasks = new ArrayList<>();
    private static Semaphore taskMutex = new Semaphore(1);
    
    private static long taskCounter = 0;
    private long id;
    private String clientID;
    private String message;
    private Status status;
    
    public ClientTask(String clientID, String message)
    {
        this.clientID = clientID;
        this.message = message;
        this.status = Status.WAITING;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getClientID()
    {
        return clientID;
    }
    
    public void setClientID(String clientID)
    {
        this.clientID = clientID;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public void setStatus(Status status)
    {
        this.status = status;
    }
    
    public enum Status
    {
        WAITING,
        IN_PROGRESS,
        COMPLETED
    }
    
    public static void FlushTaskList()
    {
        try
        {
            taskMutex.acquire();
            clientTasks.clear();
            taskCounter = 0;
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static List<ClientTask> GetTasks()
    {
        List<ClientTask> tasks = null;
        
        try
        {
            taskMutex.acquire();
            tasks = new ArrayList<>(clientTasks);
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("ClientTask.GetTasks function has been interrupted.\n" + e);
        }
        
        return tasks;
    }
    
    public static long AddNewTask(ClientTask newTask)
    {
        newTask.setId(taskCounter);
        
        try
        {
            taskMutex.acquire();
            clientTasks.add(newTask);
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
    
    public static void AddNewTaskForMany(List<String> clients, String message)
    {
        try
        {
            taskMutex.acquire();
            for(String id : clients)
            {
                ClientTask newTask = new ClientTask(id, message);
                newTask.setId(taskCounter);
                clientTasks.add(newTask);
                taskCounter++;
            }
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("AddNewTaskForMany function has been interrupted.\n" + e);
        }
    }
    
    public static void SetTaskStatus(ClientTask task, Status status)
    {
        try
        {
            taskMutex.acquire();
            for(int i = clientTasks.size() - 1; i >= 0; i--)
            {
                if(clientTasks.get(i).getId() == task.getId())
                {
                    clientTasks.get(i).setStatus(status);
                    break;
                }
            }
            taskMutex.release();
        }
        catch(InterruptedException e)
        {
            System.err.println("SetTaskStatus function has benn interrupted.\n" + e);
        }
    }
}
