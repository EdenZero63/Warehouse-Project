package exception;

//owner: Role 8 (QA and errors)
//task is malformed (unknown label, pickup = drop-off) or too long even on a full battery
public class InvalidTaskException extends Exception{
    public InvalidTaskException(String message){
        super(message);
    }
}
