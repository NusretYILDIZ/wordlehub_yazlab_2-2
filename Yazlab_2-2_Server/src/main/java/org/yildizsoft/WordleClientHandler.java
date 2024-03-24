package org.yildizsoft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WordleClientHandler implements Runnable
{
    private Socket clientSocket;
    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private boolean shouldContinue;

    public WordleClientHandler(Socket socket)
    {
        this.clientSocket = socket;
        this.shouldContinue = true;
    }

    @Override
    public void run()
    {
        System.out.println("Yeni bir client bağlandı. Client ID: " + Thread.currentThread().threadId());
        try
        {
            clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch (IOException e)
        {
            System.err.println("Client bağlantısında bir hata oluştu.\n\n" + e);
            shouldContinue = false;
        }

        String line;
        while(shouldContinue)
        {
            try
            {
                while((line = clientReader.readLine()) != null)
                {
                    if(line.equalsIgnoreCase("quit"))
                    {
                        shouldContinue = false;
                        break;
                    }
                    else HandleClientRequest(line);
                }
            }
            catch(IOException e)
            {
                System.err.println("Client'tan gelen veri okunurken bir hata oluştu.\n\n" + e);
                shouldContinue = false;
            }
        }

        Disconnect();
    }

    public void Disconnect()
    {
        System.out.println("Client bağlantısı sonlandırılıyor...");
        try
        {
            clientReader.close();
            clientWriter.close();
            clientSocket.close();
        }
        catch(IOException e)
        {
            System.err.println("Client bağlantısı sonlandırılırken bir hata oluştu.\n\n" + e);
        }
        System.out.println("Client bağlantısı sonlandırıldı.");
    }

    public void HandleClientRequest(String req)
    {
        System.out.println(req);
    }
}
