package ui;

import java.util.Scanner;

import exception.InvalidCommandException;

//owner: Role 7 (console UI)
//all keyboard reading goes through here so bad input is handled in one place
public class InputHelper{
    //fields
    private final Scanner scanner;

    //constructor
    public InputHelper(Scanner scanner){
        this.scanner = scanner;
    }

    //prints the prompt and returns the trimmed line ("" if the user just pressed Enter)
    public String readLine(String prompt){
        //TODO (Role 7)
        throw new UnsupportedOperationException("TODO (Role 7): readLine");
    }

    //reads a whole number in min..max, throws if it isn't a number or is out of range
    public int readInt(String prompt, int min, int max) throws InvalidCommandException{
        //TODO (Role 7)
        throw new UnsupportedOperationException("TODO (Role 7): readInt");
    }
}
