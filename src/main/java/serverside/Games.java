/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverside;

import java.util.ArrayList;

/**
 *
 * @author regularclip
 */
public class Games {
    
    private ArrayList<Game> games;
    private int userCount;
    
    public Games(){
        games = new ArrayList<>();
    }
    
    public void add(Game game){
        userCount++;
        games.add(game);
        System.out.println("Total players: " + userCount);
    }
    
    //is not used
    public void remove(Game game){
        userCount--;
        games.remove(game);
    }
    
    public boolean doesGameExist(int userId){
        for(Game game: games){
            if(game.getUserId() == userId){
                return true;
            }
        }
        
        return false;
    }
    
    public int getUserCount(){
        return userCount;
    }
    
    private int fetchGuess(String request){
        if(request.equals("GET /again.html? HTTP/1.1")){
            return -1;
        } else if(request.equals("GET / HTTP/1.1")){
            return -1;
        }
        String[] arr = request.split("=");
        arr = arr[1].split(" ");
        int guess = Integer.parseInt(String.valueOf(arr[0]));
        return guess;
    }
    //"GET /submit.html?numberGuess=1 HTTP/1.1"
    public void updateGameState(int userId, String request){     
        int guess = fetchGuess(request);
        if(guess == -1){
            for(Game game: games){
                if(game.getUserId()==userId){
                    game.setCurrentGuesses(0);
                    game.setNumber();
                    break;
                }
            }

        } else {
    
            for(Game game: games){
                if(game.getUserId()==userId){
                    game.incrementGuesses();
                    game.setCurrentGuess(guess);
                    break;
                }
            }
        }
    }
    
    public String getGameState(int userId){
        StringBuilder gameStateHtml = new StringBuilder();
        Game currentGame = new Game();
        for(Game game: games){
            if(game.getUserId() == userId){
                currentGame = game;
                break;
            }
        }

        //Check if user needs to guess higher or lower
        String lowHigh = null;
        if(currentGame.getCurrentGuess() < currentGame.theNumber()){
            lowHigh = "Higher";
        } else {
            lowHigh = "Lower";
        }
        
        gameStateHtml.append("<!DOCTYPE html>");
        gameStateHtml.append("<html>");
        gameStateHtml.append("<head></head>");
        gameStateHtml.append("<body>");
        gameStateHtml.append("<div>");
        
        if(currentGame.getGuesses() == 0){
            gameStateHtml.append("<h1>Welcome to the guessing game!</h1>");
            gameStateHtml.append("<h2>Im thinking of a number between 1 - 100</h2>");
            gameStateHtml.append("<h2>care to guess?</h2>");
            gameStateHtml.append("<form action='/submit.html' method='GET'>");
            gameStateHtml.append("Guess: <input type='text' name='numberGuess'>");
            gameStateHtml.append("<input type='submit' value='Submit!'>");
            gameStateHtml.append("</form>");
        } else if(currentGame.hasWon()){
            startNewGame(userId);
            gameStateHtml.append("<h1>Congratulations!</h1>");
            gameStateHtml.append("<h2>You guessed the corrent number!</h2>");
            gameStateHtml.append("<h2>It was "+ currentGame.theNumber() +" </h2>");
            gameStateHtml.append("<h2>And you did it in "+currentGame.getGuesses()+ " tries </h2>");
            gameStateHtml.append("<h2>Care to try again?</h2>");
            gameStateHtml.append("<form action='/again.html' method='GET'>");
            gameStateHtml.append("<input type='submit' value='Yes'>");
            gameStateHtml.append("</form>");
        } else {
            gameStateHtml.append("<h1>Well Done!</h1>");
            gameStateHtml.append("<h2>You correctly guessed the wrong number!</h2>");
            gameStateHtml.append("<h2>Attempts: "+ currentGame.getGuesses() +"</h2>");
            gameStateHtml.append("<h2>Please guess something a little "+ lowHigh +"</h2>");
            gameStateHtml.append("<form action='/submit.html' method='GET'>");
            gameStateHtml.append("Guess: <input type='text' name='numberGuess'>");
            gameStateHtml.append("<input type='submit' value='Submit!'>");
            gameStateHtml.append("</form>");
        }        
        
        gameStateHtml.append("</div>");
        gameStateHtml.append("</body>");
        gameStateHtml.append("</html>");
        return gameStateHtml.toString();
    }
    
    private void startNewGame(int userId){
        for(Game game: games){
            if(game.getUserId()==userId){
                game = new Game(userId);
            }
        }
    }
    
    
}

