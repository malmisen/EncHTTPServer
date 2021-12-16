/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package serverside;

/**
 *
 * @author regularclip
 */
public class Game {
    private int userId;
    private int guesses;
    private int theNumber;
    private int currentGuess;
    
    public Game(){}
    
    public Game(int userId){
        this.userId = userId;
        guesses = 0;
        currentGuess = 0;
        theNumber = (int)(Math.random() * 100);  
        //theNumber = 0;
    }
    
    public int getUserId(){
        return userId;
    }
    
    public int getGuesses(){
        return guesses;
    }
    
    
    public int theNumber(){
        return theNumber;
    }
    
    public void setNumber(){
        theNumber = (int)(Math.random() * 100);
    }
    
    public void incrementGuesses(){
        guesses++;
    }
    
    public void setCurrentGuess(int guess){
        currentGuess = guess;
    }
    
    public boolean hasWon(){
        return currentGuess == theNumber;
    }
    
    public int getCurrentGuess(){
        return currentGuess;
    }

    void setCurrentGuesses(int i) {
        guesses = i;
    }
    
    public int getNumber(){
        return theNumber;
    }
}