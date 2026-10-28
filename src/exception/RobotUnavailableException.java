package exception;

//owner: Role 8 (QA and errors)
//robot ID doesn't exist, or the robot can't take work right now (e.g. stranded)
public class RobotUnavailableException extends Exception{
    public RobotUnavailableException(String message){
        super(message);
    }
}
