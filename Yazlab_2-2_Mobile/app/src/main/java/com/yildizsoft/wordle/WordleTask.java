package com.yildizsoft.wordle;

public class WordleTask
{
    private String task;
    private WordleTaskType taskType;

    public WordleTask(WordleTaskType type, String task)
    {
        this.task = task;
        this.taskType = type;
    }

    public String getTask()
    {
        return task;
    }

    public void setTask(String task)
    {
        this.task = task;
    }

    public WordleTaskType getTaskType()
    {
        return taskType;
    }

    public void setTaskType(WordleTaskType taskType)
    {
        this.taskType = taskType;
    }
}
