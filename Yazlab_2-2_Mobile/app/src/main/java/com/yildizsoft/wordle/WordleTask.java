package com.yildizsoft.wordle;

import java.util.List;

public class WordleTask
{
    private List<String> contents;
    private WordleTaskType task;

    public WordleTask(WordleTaskType task, List<String> contents)
    {
        this.contents = contents;
        this.task = task;
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
}
