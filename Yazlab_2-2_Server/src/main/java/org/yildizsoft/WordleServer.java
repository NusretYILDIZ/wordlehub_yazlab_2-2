package org.yildizsoft;

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

    private static final String mongoDbUrl = "mongodb://localhost:27017";
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public WordleServer(int port)
    {
        this.serverPort = port;
    }

    public void Start()
    {
        try
        {
            System.out.println("\nWordle sunucusu başlatılıyor...");
            serverSocket = new ServerSocket(serverPort);

            mongoClient = MongoClients.create("mongodb://localhost:27017");
            mongoDatabase = mongoClient.getDatabase("yazlab_2-2");

            System.out.println("Wordle sunucusu başarıyla başlatıldı.");

            MongoCollection<Document> collection = mongoDatabase.getCollection("yazlab_2-2");
            FindIterable<Document> docs = collection.find();

            for(Document doc : docs)
            {
                System.out.println(doc.toJson());
            }

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
