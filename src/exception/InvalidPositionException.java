package exception;

//owner: Role 8 (QA and errors)
//position is off the grid, or a label like "P9" doesn't exist on the map
public class InvalidPositionException extends Exception{
    public InvalidPositionException(String message){
        super(message);
    }
}
