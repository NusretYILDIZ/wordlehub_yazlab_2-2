package org.yildizsoft;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordleClientHandler implements Runnable
{
    private Socket clientSocket;
    //private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private BufferedWriter clientWriter;
    private boolean shouldContinue;
    private long id;
    private int failCount;

    public WordleClientHandler(Socket socket)
    {
        this.clientSocket = socket;
        this.shouldContinue = true;
        this.failCount = 0;
    }

    @Override
    public void run()
    {
        this.id = Thread.currentThread().threadId();
        System.out.println("Yeni bir client bağlandı. Client ID: " + this.id);
        try
        {
            //clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
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
                /*while((line = clientReader.readLine()) != null)
                {
                    if(line.equalsIgnoreCase("quit"))
                    {
                        shouldContinue = false;
                        break;
                    }
                    else HandleClientRequest(line);
                }*/
                try
                {
                    clientWriter.write("mrb");
                }
                catch(IOException e)
                {
                    this.failCount++;

                    if(this.failCount >= 3)
                    {
                        System.err.println(this.id + " ID'li client'ın bağlantısı kopmuş olabilir.");
                        Disconnect();
                        return;
                    }
                }

                if(this.failCount == 0)
                {
                    line = clientReader.readLine();
                    if(line != null)
                    {
                        if(line.equalsIgnoreCase("quit"))
                        {
                            Disconnect();
                            return;
                        }
                        else HandleClientRequest(line);
                    }
                }
            }
            catch(IOException e)
            {
                System.err.println(this.id + " ID'li client'tan veri okunurken bir hata oluştu.\n\n" + e);
                shouldContinue = false;
            }
        }

        Disconnect();
    }

    public void Disconnect()
    {
        System.out.println(this.id + " ID'li client bağlantısı sonlandırılıyor...");
        try
        {
            clientReader.close();
            clientWriter.close();
            clientSocket.close();
        }
        catch(IOException e)
        {
            System.err.println(this.id + " ID'li client bağlantısı sonlandırılırken bir hata oluştu.\n\n" + e);
        }
        System.out.println(this.id + " ID'li client bağlantısı sonlandırıldı.");
    }

    public void HandleClientRequest(String req) throws IOException {
        System.out.println(req);
        List<String> tokens = new ArrayList<>(Arrays.asList(req.split("\"")));
        String task = tokens.getFirst();
        tokens.removeFirst();

        switch(task)
        {
            case "SIGNUP":
                int res = MongoManager.AddUser(tokens);
                if(res == 1)
                {
                    clientWriter.write("SIGNUP_FAIL_USER_ALREADY_EXISTS");
                }
                break;

            case "LOGIN":
                if(MongoManager.UserExists(tokens))
                {
                    System.out.println("ayca_" + this.id + " oturum açtı.");
                }
                else
                {
                    System.err.println("ayca_" + this.id + "'in oturum bilgileri geçersiz.");
                }
                break;
        }
    }
}
