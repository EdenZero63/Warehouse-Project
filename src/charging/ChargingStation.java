package charging;

import java.util.ArrayList;

import robot.Robot;
import sim.Simulation;
import warehouse.Position;

//owner: Role 4 (charging)
//one charger cell; holds one robot at a time, the rest wait in arrival order (Rule C)
public class ChargingStation{
    //fields
    private final String id; //"C1"
    private final Position position;
    private Robot occupant; //null when free
    private final ArrayList<Robot> waiting;

    //constructor, starts free with an empty line
    public ChargingStation(String id, Position position){
        this.id = id;
        this.position = position;
        this.occupant = null;
        this.waiting = new ArrayList<Robot>();
    }

    //getters
    public String getId(){
        return id;
    }

    public Position getPosition(){
        return position;
    }

    public Robot getOccupant(){
        return occupant;
    }

    //free and queue checks
    public boolean isFree(){
        return occupant == null;
    }

    public int getQueueSize(){
        return waiting.size();
    }

    //robot reached the charger: take the spot if free (ChargingState), else join the line (WaitingForChargerState)
    public void arrive(Robot robot){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): arrive");
    }

    //once per tick: add CHARGE_RATE to the occupant, release it at MAX_BATTERY
    //(or RELEASE_LEVEL if someone is waiting), then resumeAfterCharging() and let the next robot in
    public void update(Simulation sim){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): update");
    }
}
