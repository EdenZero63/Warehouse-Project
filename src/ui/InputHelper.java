package ui;

import java.util.Scanner;

import exception.InvalidCommandException;

//all the keyboard input goes through here so bad input only gets handled in one spot
public class InputHelper{
    //fields
    private final Scanner scanner;

    //constructor
    public InputHelper(Scanner scanner){
        this.scanner = scanner;
    }

    //prints the prompt and gives back the line without extra spaces ("" if you just hit Enter)
    //if the input ends (like Ctrl+Z) it returns "q" so the menu just quits instead of crashing
    public String readLine(String prompt){
        System.out.print(prompt);
        if(!scanner.hasNextLine()){
            System.out.println();
            return "q";
        }
        return scanner.nextLine().trim();
    }

    //reads a number between min and max, throws if you type something else or it's out of range
    public int readInt(String prompt, int min, int max) throws InvalidCommandException{
        return parseInt(readLine(prompt), min, max);
    }

    //same idea but for text we already have, like the 5 in "n 5"
    public int parseInt(String text, int min, int max) throws InvalidCommandException{
        int value;
        try{
            value = Integer.parseInt(text.trim());
        }catch(NumberFormatException e){
            throw new InvalidCommandException("\"" + text + "\" is not a whole number");
        }
        if(value < min || value > max){
            throw new InvalidCommandException("pick a number from " + min + " to " + max);
        }
        return value;
    }
}
