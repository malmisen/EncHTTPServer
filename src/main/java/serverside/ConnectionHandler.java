/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author regularclip
 */
public class ConnectionHandler implements Runnable {
    
    private Socket con;
    private BufferedReader reader;
    private PrintStream stream;
    private StringBuilder str;
    private Games games;
    private int cookie;
    
    //Codes
    private final String OK             = "HTTP/1.1 200 OK";
    private final String NOTFOUND       = "HTTP/1.1 404 Not Found";
    private final String ERROR          = "HTTP/1.1 400 Bad Request";
    //Headers
    private final String SERVER         = "Server: myLittleServer";
    private final String CONTENTTYPE    = "Content-Type: text/html";
    private final String COOKIE         = "Set-Cookie: clientId=";

    public ConnectionHandler(Socket connectionSocket, Games games) {
        con = connectionSocket;
        str = new StringBuilder();
       // cookie = userId;
        this.games = games;
       // cookie = userId;
    }
   
    private void sendHeaders(String request, boolean hasCookie, PrintStream stream) throws IOException{
        if(request.equals("GET / HTTP/1.1")){
             stream.println(OK);
             stream.println(CONTENTTYPE);
             stream.println(SERVER);
            if(!hasCookie){
                System.out.println("I AM SETTING THE COOKIE!");
                stream.println(COOKIE + String.valueOf(cookie));  
            } 

        } else if (request.equals("GET /favicon.ico HTTP/1.1")) {
            stream.println(NOTFOUND);
         
        } else if (request.split("\\?")[0].equals("GET /submit.html")){
            stream.println(OK);
            stream.println(CONTENTTYPE);
            stream.println(SERVER);
            
        } else if (request.equals("GET /again.html")){
            stream.println(OK);
            stream.println(CONTENTTYPE);
            stream.println(SERVER);
        } else {
          stream.println(ERROR);
        }
          stream.print("\r\n");
          stream.flush();
    }

    private void sendContent(PrintStream stream, int cookie) throws IOException{ 
        String content = games.getGameState(cookie);
        stream.println(content);
        stream.flush();
    }
    
    private void startGame(){
            Game game = new Game(cookie);
            games.add(game);
            
            System.out.println("New game created!");
            System.out.println("User: " + cookie);
            System.out.println("The number: " + game.getNumber());
    }
    
    
    public int getUserId(String arr){
        String[] req = arr.split("Cookie: clientId=");
        req = req[1].split("\n");
        String user = String.valueOf(req[0]);
        return Integer.parseInt(user);
    }
    
    private String[] getClientHeaders(String clientRequest){
        String[] clientHeaders = clientRequest.split("\n");
        return clientHeaders;
    }
    
    private boolean searchCookie(String arr[]){
        StringBuilder cookie = new StringBuilder();
        for(String header: arr){
            for(int i = 0; i < 6; i++){
                cookie.append(header.charAt(i));
            }
            if(cookie.toString().contains("Cookie")) return true;
        }
        return false;
    }
   
    @Override
    public void run()  {
        try {
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String msg = "";
            
            //read client request
            while(true){
                msg = reader.readLine();
               // System.out.println("Msg:" + msg);
                if(msg != null){
                    str.append(msg + "\n");
                    if(msg.equals("")){
                        break;
                    }
                }
            }
            //create new writer to the outputstream
            stream = new PrintStream(con.getOutputStream());
            
            //the client headers neatly stacked in a list
            String[] clientHeaders = getClientHeaders(str.toString());
            
            //is there a cookie set among the client request headers?
            boolean hasCookie = searchCookie(clientHeaders);
          
            //check if cookie is set
            if(!hasCookie){
                //request a userid/cookie from game
                cookie = games.getUserCount();
                // start new game
                startGame();
            } else {
                //else get the client cookie and update the game state
                boolean gameExists = games.doesGameExist(cookie);
                if(!gameExists){
                    startGame();
                }
                cookie = getUserId(str.toString());
                games.updateGameState(cookie, clientHeaders[0]);
            }
            
            //send response headers to client
            sendHeaders(clientHeaders[0], hasCookie, stream);
            
            //send the html content to the client
            sendContent(stream, cookie);
            
            //close the stream
            stream.close();
            
            //close the socket
            con.close();
    
            
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
