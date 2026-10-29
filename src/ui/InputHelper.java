package ui;

import java.util.Scanner;

import exception.InvalidCommandException;

//all keyboard reading goes through here so bad input is handled in one place
public class InputHelper{
    //fields
    private final Scanner scanner;

    //constructor
    public InputHelper(Scanner scanner){
        this.scanner = scanner;
    }

    //prints the prompt and returns the trimmed line ("" if the user just pressed Enter)
    //returns "q" if input has ended (e.g. Ctrl+Z) so the menu quits instead of crashing
    public String readLine(String prompt){
        System.out.print(prompt);
        if(!scanner.hasNextLine()){
            System.out.println();
            return "q";
        }
        return scanner.nextLine().trim();
    }

    //reads a whole number in min..max, throws if it isn't a number or is out of range
    public int readInt(String prompt, int min, int max) throws InvalidCommandException{
        return parseInt(readLine(prompt), min, max);
    }

    //turns text into a whole number in min..max, for commands like "n 5"
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
