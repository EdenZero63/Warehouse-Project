package exception;

//owner: Role 8 (QA and errors)
//user typed a menu command that doesn't exist or is malformed
public class InvalidCommandException extends Exception{
    public InvalidCommandException(String message){
        super(message);
    }
}
