package org.yildizsoft;

import com.mongodb.MongoException;
import com.mongodb.client.*;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class WordleServer
{
    private ServerSocket serverSocket;
    private int serverPort;

    public WordleServer(int port)
    {
        this.serverPort = port;
    }

    public void Start()
    {
        try
        {
            MongoManager.Start();
        }
        catch(MongoException e)
        {
            System.err.println("MongoDB'ye bağlanırken bir sorun oluştu.\n\n" + e);
            return;
        }

        try
        {
            System.out.println("\nWordle sunucusu başlatılıyor...");
            serverSocket = new ServerSocket(serverPort);
            System.out.println("\nWordle sunucusu başarıyla başlatıldı.");

            while (true)
            {
                new Thread(new WordleClientHandler(serverSocket.accept())).start();
            }
        }
        catch(IOException e)
        {
            System.err.println("Wordle sunucusu başlatılırken bir hata oluştu.\n\n" + e);
        }
    }
}
