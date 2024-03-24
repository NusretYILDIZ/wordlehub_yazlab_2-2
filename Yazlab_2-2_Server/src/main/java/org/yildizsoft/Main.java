package org.yildizsoft;

public class Main
{
    public static void main(String[] args)
    {
        WordleServer wordleServer = new WordleServer(65535);
        wordleServer.Start();
    }
}