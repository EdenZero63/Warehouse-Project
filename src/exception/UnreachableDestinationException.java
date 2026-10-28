package exception;

//owner: Role 8 (QA and errors)
//no path exists between two cells because obstacles wall it off
public class UnreachableDestinationException extends Exception{
    public UnreachableDestinationException(String message){
        super(message);
    }
}
