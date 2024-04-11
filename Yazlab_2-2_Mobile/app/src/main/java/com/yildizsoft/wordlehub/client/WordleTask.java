package com.yildizsoft.wordlehub.client;

import java.util.List;

public class WordleTask
{
    private List<String> contents;
    private WordleTaskType task;
    private boolean isProcessing;

    public WordleTask(WordleTaskType task, List<String> contents)
    {
        this.contents = contents;
        this.task = task;
        this.isProcessing = false;
    }

    public List<String> getContents()
    {
        return contents;
    }

    public void setContents(List<String> contents)
    {
        this.contents = contents;
    }

    public WordleTaskType getTask()
    {
        return task;
    }

    public void setTask(WordleTaskType task)
    {
        this.task = task;
    }

    public boolean isProcessing()
    {
        return isProcessing;
    }

    public void setProcessing(boolean processing)
    {
        isProcessing = processing;
    }
}
