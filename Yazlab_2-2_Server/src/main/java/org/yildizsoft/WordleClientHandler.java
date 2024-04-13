package org.yildizsoft;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WordleClientHandler implements Runnable
{
    private final Socket         clientSocket;
    private       BufferedReader clientReader;
    private       BufferedWriter clientWriter;
    private       PrintWriter    clientPrinter;
    private       boolean        shouldContinue;
    private       String         id;
    private       int            failCount;
    
    private ScheduledFuture<?> scheduledFuture;
    private ScheduledExecutorService scheduledExecutorService;
    
    public WordleClientHandler(Socket socket)
    {
        this.clientSocket   = socket;
        this.shouldContinue = true;
        this.failCount      = 0;
    }
    
    @Override
    public void run()
    {
        try
        {
            clientPrinter = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriter  = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            clientReader  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.id = clientReader.readLine();
        }
        catch(IOException e)
        {
            System.err.println("Client bağlanırken bir hata oluştu.\n" + e);
            shouldContinue = false;
        }
        LogMessage("Yeni bir client bağlandı. Client ID: " + this.id);
        
        boolean networkError = false;
        String line;
        while(shouldContinue)
        {
            try
            {
                try
                {
                    clientWriter.write("mrb");
                    
                    if(networkError)
                    {
                        LogMessage("Tekrar bağlantı kuruldu.");
                        networkError = false;
                        if(scheduledFuture != null) scheduledFuture.cancel(false);
                        if(scheduledExecutorService != null) scheduledExecutorService.shutdownNow();
                    }
                }
                catch(IOException e)
                {
                    this.failCount++;
                    
                    if(this.failCount >= 3)
                    {
                        LogError("Bağlantı kopmuş olabilir. Oturum 10 saniye içinde kapatılacak.");
                        //Disconnect();
                        if(!networkError)
                        {
                            networkError = true;
                            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                            scheduledFuture = scheduledExecutorService.schedule(this::Disconnect, 10, TimeUnit.SECONDS);
                        }
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
                LogError("Veri okunurken bir hata oluştu.\n" + e);
                shouldContinue = false;
                Disconnect();
            }
        }
        
        //Disconnect();
    }
    
    public void Disconnect()
    {
        LogMessage("Bağlantı sonlandırılıyor...");
        try
        {
            OnlinePlayers.RemoveOnlinePlayer(this.id);
            clientReader.close();
            clientPrinter.close();
            clientWriter.close();
            clientSocket.close();
        }
        catch(IOException e)
        {
            LogError("Bağlantı sonlandırılırken bir hata oluştu.\n" + e);
        }
        LogMessage("Bağlantı sonlandırıldı.");
    }
    
    public void HandleClientRequest(String req) throws IOException
    {
        LogMessage(req);
        List<String> tokens = new ArrayList<>(Arrays.asList(req.split("\"")));
        String       task   = tokens.getFirst();
        tokens.removeFirst();
        
        switch(task)
        {
        case "SIGNUP":
            SignUpTask(tokens);
            break;
        
        case "LOGIN":
            LoginTask(tokens);
            break;
            
        case "LOGOUT":
            LogoutTask(tokens);
            break;
            
        case "ENTER_LOBBY":
            EnterLobbyTask(tokens);
            break;
        
        case "EXIT_LOBBY":
            ExitLobbyTask(tokens);
            break;
            
        case "PLAYER_LIST":
            PlayerListTask(tokens);
            break;
            
        case "SEND_GAME_REQUEST":
            SendGameRequestTask(tokens);
            break;
        }
    }
    
    public void SignUpTask(List<String> tokens)
    {
        int res = MongoManager.AddUser(tokens.getFirst(), tokens.get(1));
        if(res == 1)
        {
            clientPrinter.println("SIGNUP_FAIL_USER_ALREADY_EXISTS");
        }
        else if(res == 0)
        {
            clientPrinter.println("SIGNUP_SUCCESS");
        }
        else
        {
            clientPrinter.println("SIGNUP_FAIL_OTHER");
        }
    }
    
    public void LoginTask(List<String> tokens)
    {
        if(!MongoManager.UsernameExists(tokens.getFirst()))
        {
            clientPrinter.println("LOGIN_FAIL_USERNAME_NOT_FOUND");
            LogError("Yanlış kullanıcı adı girdi ve oturum açamadı.");
        }
        else if(!MongoManager.UserExists(tokens.getFirst(), tokens.get(1)))
        {
            clientPrinter.println("LOGIN_FAIL_WRONG_PASSWORD");
            LogError("Yanlış şifre girdi ve oturum açamadı.");
        }
        else
        {
            OnlinePlayers.NewOnlinePlayer(new PlayerInfo(this.id, tokens.getFirst()));
            clientPrinter.println("LOGIN_SUCCESS\"" + this.id + '"' + tokens.getFirst());
            LogMessage('"' + tokens.getFirst() + "\" kullanıcı adıyla oturum açtı.");
        }
    }
    
    public void LogoutTask(List<String> tokens)
    {
        OnlinePlayers.RemoveOnlinePlayer(this.id);
        clientPrinter.println("LOGOUT_SUCCESS");
        LogMessage("Oturum kapatıldı.");
    }
    
    public void EnterLobbyTask(List<String> tokens)
    {
        PlayerInfo player = OnlinePlayers.GetOnlinePlayerByID(this.id);
        
        if(player != null)
        {
            String constLetter = tokens.getFirst();
            String letterCount = tokens.get(1);
            PlayerLobby lobby = PlayerLobby.valueOf((constLetter.equalsIgnoreCase("NO") ? "NO" : "WITH") + "_CONST_" + letterCount + "_LETTER");
            
            OnlinePlayers.SetLobbyOfPlayer(player, lobby);
            clientPrinter.println("ENTER_LOBBY_SUCCESS\"" + lobby);
            LogMessage("Başarıyla odaya girdi.");
        }
        else
        {
            clientPrinter.println("LOGIN_REQUIRED");
            LogError("Oturum açmadan odaya girmeye çalışıyor.");
        }
    }
    
    public void ExitLobbyTask(List<String> tokens)
    {
        if(OnlinePlayers.GetOnlinePlayerByID(this.id) != null && OnlinePlayers.GetOnlinePlayerByID(this.id).getLobby() != null)
        {
            OnlinePlayers.SetLobbyOfPlayer(OnlinePlayers.GetOnlinePlayerByID(this.id), null);
            clientPrinter.println("EXIT_LOBBY_SUCCESS");
            LogMessage("Odadan çıktı ve oda seçim ekranına döndü.");
        }
        else
        {
            clientPrinter.println("EXIT_LOBBY_FAIL");
            LogMessage("Odadan çıkamadı.");
        }
    }
    
    public void PlayerListTask(List<String> tokens)
    {
        PlayerInfo player = OnlinePlayers.GetOnlinePlayerByID(this.id);
        
        if(player != null)
        {
            ArrayList<PlayerInfo> players = OnlinePlayers.GetPlayersInLobby(player.getLobby());
            
            if(players.isEmpty())
            {
                clientPrinter.println("NO_PLAYERS");
                //LogMessage(player.getLobby() + " odasında oyuncu yok.");
            }
            else
            {
                StringBuilder builder = new StringBuilder("PLAYERS_IN_LOBBY");
                
                for(PlayerInfo playerInLobby : players) builder.append('"').append(playerInLobby.getUsername()).append('"').append(playerInLobby.getStatus());
                
                clientPrinter.println(builder);
                //LogMessage(player.getLobby() + " odasındaki oyuncular: " + builder);
            }
        }
        else
        {
            clientPrinter.println("LOGIN_REQUIRED");
            LogError("Oturum açmadan oyuncu listesini görmeye çalışıyor.");
        }
    }
    
    public void SendGameRequestTask(List<String> tokens)
    {
        clientPrinter.println("SUCCESS");
        // TODO: Handle the game request properly.
    }
    
    public void LogMessage(String msg)
    {
        PlayerInfo playerInfo = OnlinePlayers.GetOnlinePlayerByID(this.id);
        System.out.println('[' + this.id + (playerInfo == null ? "] " : " (" + playerInfo.getUsername() + ")] ") + msg);
    }
    
    public void LogError(String msg)
    {
        System.err.println('[' + this.id + "] " + msg);
    }
}
