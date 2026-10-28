package charging;

import robot.Robot;
import sim.Simulation;
import task.Task;
import warehouse.Position;

//owner: Role 4 (charging)
//every battery number and rule lives here (DESIGN.md section 4)
//distances always come from sim.getPathFinder(), never straight-line
public class ChargingPolicy{
    //battery numbers
    public static final int MAX_BATTERY = 100;
    public static final int MOVE_COST = 2;
    public static final int WAIT_COST = 0;
    public static final int CHARGE_RATE = 15;
    public static final int RESERVE = 6;
    public static final int RELEASE_LEVEL = 80;

    //Rule A: robot -> pickup -> drop-off -> nearest charger and still RESERVE left
    public boolean canAcceptTask(Robot robot, Task task, Simulation sim){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): canAcceptTask");
    }

    //Rule A, second half: could any robot do this task starting full at a charger
    //if not, TaskManager rejects the task when it's created
    public boolean isPossibleOnFullBattery(Task task, Simulation sim){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): isPossibleOnFullBattery");
    }

    //Rule B, point of no return: would stepping to `next` leave too little to reach a charger + RESERVE
    public boolean mustGoCharge(Robot robot, Position next, Simulation sim){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): mustGoCharge");
    }

    //nearest charger by path length, null if none can be reached
    public ChargingStation findNearestCharger(Position from, Simulation sim){
        //TODO (Role 4): loop over sim.getChargers(), compare pathFinder.distance(...)
        throw new UnsupportedOperationException("TODO (Role 4): findNearestCharger");
    }
}
