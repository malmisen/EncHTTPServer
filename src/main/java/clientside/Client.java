/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clientside;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 *
 * @author regularclip
 */
public class Client {
    
   
   
    public static void main(String[] args) throws IOException, InterruptedException {
       
       int totalAttempts = 0;
        int count = 0;
        while(count != 100){
            
            //Setup
            int arr[] = {0, 100, 50};
            boolean correct = false;
            int attempts = 0;
            
            //intial request
            URL url = new URL("http://localhost:8080/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
        
            int cookie = requestInitialPage(con);
            con.disconnect();
            //intial request done and cookie has been fetched.
            
         
        
            //PLAY GAME
            while(!correct){
                url = new URL("http://localhost:8080/submit.html?numberGuess=" + String.valueOf(arr[2]));
               // System.out.println("MY GUESS: " + arr[2]);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                con.setRequestProperty("Cookie", "clientId="+String.valueOf(cookie));
                String respons = playGame(con); // read server response
                attempts++;
                if(respons.contains("Higher")){
                    arr = getNextGuess(arr, "Higher");
                } else if(respons.contains("Lower")){
                    arr = getNextGuess(arr, "Lower");
                }
                else{
                    correct = true;
                }
        }
            totalAttempts += attempts;
           // System.out.println("DONE! " +  count);
           // System.out.println("User: " + cookie);
           // System.out.println("Attempts: " + attempts + "\r\n");
        count++;
        }
        
        System.out.println("Average guesses: " + totalAttempts/100);
            
   
    }
    
    private static int[] getNextGuess(int[] arr, String highLow){
        
        int min = arr[0];
        int max = arr[1];
        int guess = arr[2];

        if(highLow.equals("Higher")){
            
            min = guess;
            guess = guess + (max - min)/2;
            
            if(guess == 99){
                guess = 100;
            }
            
        } else if (highLow.equals("Lower")){
            
           max = guess;
           guess = guess - (max - min)/2;
           
           if(guess == 1){
               guess = 0;
           }
        } 
        
        arr[0] = min;
        arr[1] = max;
        arr[2] = guess;
        return arr;
    
    }
    
    private static int requestInitialPage(HttpURLConnection con) throws ProtocolException, IOException{
        StringBuilder str = new StringBuilder();
        String msg = "";
        String cookie = "";
        con.setRequestMethod("GET");
        int statusCode = con.getResponseCode();
        if(statusCode == HttpURLConnection.HTTP_OK){
            
            String[] cookieField = con.getHeaderField(3).split("=");
            cookie = cookieField[1];
            System.out.println("COOKIE: " + cookie);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while((msg = reader.readLine()) != null){
                str.append(msg);
            }
            reader.close();
        }
       return Integer.parseInt(cookie);
    }
    
    private static String playGame(HttpURLConnection con) throws ProtocolException, IOException{

        StringBuilder str = new StringBuilder();
        String msg = "";
        int statusCode = con.getResponseCode();
        if(statusCode == HttpURLConnection.HTTP_OK){
 
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while((msg = reader.readLine()) != null){
                str.append(msg);
            }
            reader.close();
        }
        
        return str.toString();
    }
    
}
