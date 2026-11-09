package robot;

import java.util.ArrayList;

import charging.Battery;
import charging.ChargingPolicy;
import exception.UnreachableDestinationException;
import sim.Simulation;
import task.Task;
import warehouse.Position;

//owner: Role 3 (robot behavior)
//a robot's data; what it does each tick is decided by its current RobotState
public class Robot{
    //how many ticks in a row a robot stays blocked before it looks for a way around
    private static final int MAX_WAITS = 3;

    //fields
    private final String id; //"R1"
    private Position position;
    private final Battery battery;
    private RobotState state;
    private Task currentTask; //null when it has no job
    private boolean carrying;
    private ArrayList<Position> path; //remaining steps to the current target
    private int waitTicks; //ticks blocked in a row, re-plan after 3

    //constructor, every robot starts idle with nothing in hand
    public Robot(String id, Position start, int batteryLevel){
        this.id = id;
        this.position = start;
        this.battery = new Battery(batteryLevel);
        this.state = new IdleState();
        this.currentTask = null;
        this.carrying = false;
        this.path = new ArrayList<Position>();
        this.waitTicks = 0;
    }

    //called once per tick by Simulation, the state decides what happens
    public void update(Simulation sim){
        state.act(this, sim);
    }

    //moves one cell along `path` if the next cell is free, paying MOVE_COST
    //returns false and counts a wait if another robot is in the way (DESIGN.md section 5, step 4)
    public boolean stepAlongPath(Simulation sim){
        Position nextStep = path.get(0);

        //check if next step is close enough to step to
        if (Math.abs(position.getRow() - nextStep.getRow()) > 1 && Math.abs(position.getCol() - nextStep.getCol()) > 1) {
            //step is too far away
            throw new UnsupportedOperationException("stepTooFar");
        }

        //check if next step is occupied
        if (!sim.getWarehouse().isWalkable(nextStep)) {
            //path obstructed by obstacle or out-of-bounds
            throw new UnsupportedOperationException("stepObstructed");
        }

        //check for robots in the way of next step
        ArrayList<Robot> robotList = sim.getRobots();
        for (Robot robot : robotList) {
            if (robot != this && robot.getPosition().equals(nextStep)) {
                //an idle robot would never move out of the way on its own, so it trades cells with us
                if (robot.isIdle()) {
                    robot.placeAt(position);
                    break;
                }
                //head-on and nobody can afford to go around: squeeze past each other, both pay for the move
                //only the later robot in ID order starts it, the earlier one has already acted this tick
                if (waitTicks >= MAX_WAITS && id.compareTo(robot.id) > 0 && robot.isHeadingInto(position)) {
                    robot.takeStep();
                    break;
                }
                //robot is in the way
                waitTicks++;
                return false;
            }
        }

        //if above tests pass, robot may move
        takeStep();
        return true;
    }

    //moves onto the next cell of the path and pays for it
    private void takeStep(){
        position = path.remove(0);
        battery.consumeTileMovement();
        waitTicks = 0;
    }

    //true if this robot's next step is `cell`
    private boolean isHeadingInto(Position cell){
        return !path.isEmpty() && path.get(0).equals(cell);
    }

    //makes sure path leads to target: plans one if there's none, looks for a way around after MAX_WAITS
    //returns false if there's nowhere to step right now, the robot just tries again next tick
    public boolean prepareRoute(Position target, Simulation sim){
        try{
            if(path.isEmpty()){
                path = sim.getPathFinder().findPath(sim.getWarehouse(), position, target);
            }else if(waitTicks >= MAX_WAITS){
                ArrayList<Position> detour = sim.getPathFinder().findPath(sim.getWarehouse(), position, target, otherRobotCells(target, sim));
                //waiting is free, so only drive around if it's really a new way and the battery covers it
                boolean newWay = !detour.get(0).equals(path.get(0));
                if(newWay && detour.size() * ChargingPolicy.MOVE_COST + ChargingPolicy.RESERVE <= battery.getLevel()){
                    path = detour;
                    waitTicks = 0;
                }
            }
        }catch(UnreachableDestinationException e){
            //no way around right now, keep the old path and keep waiting
        }
        return !path.isEmpty();
    }

    //every other robot's cell except the target, the target has to stay enterable or there's never a path
    private ArrayList<Position> otherRobotCells(Position target, Simulation sim){
        ArrayList<Position> cells = new ArrayList<Position>();
        for(int i = 0; i < sim.getRobots().size(); i++){
            Robot other = sim.getRobots().get(i);
            if(other != this && !other.position.equals(target)){
                cells.add(other.position);
            }
        }
        return cells;
    }

    //Rule B: head for a charger, currentTask and carrying stay as they are so the job can resume
    public void goCharge(Simulation sim){
        path = new ArrayList<Position>();
        waitTicks = 0;
        state = new ToChargerState();
        sim.getLog().add(sim.getTick(), id + " low battery, heading to a charger");
    }

    //Rule D: out of battery, stop here; the task goes back in line, or fails if the item was on board
    public void strand(Simulation sim){
        if(currentTask != null){
            if(carrying){
                currentTask.markFailed("robot stranded");
                carrying = false;
            }else{
                currentTask.returnToPending();
            }
        }
        path = new ArrayList<Position>();
        waitTicks = 0;
        state = new StrandedState();
        sim.getLog().add(sim.getTick(), id + " stranded, out of battery");
    }

    //puts the robot straight on a cell for free, only for trading cells next to a charger or a parked robot
    public void placeAt(Position cell){
        position = cell;
        path = new ArrayList<Position>();
        waitTicks = 0;
    }

    //after charging: carry on with the saved task (drop-off if carrying, else pickup), or go idle
    public void resumeAfterCharging(Simulation sim){
        path = new ArrayList<Position>();
        waitTicks = 0;
        if(currentTask == null){
            state = new IdleState();
        }else if(carrying){
            state = new ToDropoffState();
        }else{
            state = new ToPickupState();
        }
    }

    //status checks
    public boolean isIdle(){
        return state instanceof IdleState;
    }

    //getters
    public String getId(){
        return id;
    }

    public Position getPosition(){
        return position;
    }

    public Battery getBattery(){
        return battery;
    }

    public RobotState getState(){
        return state;
    }

    public Task getCurrentTask(){
        return currentTask;
    }

    public boolean isCarrying(){
        return carrying;
    }

    public ArrayList<Position> getPath(){
        return path;
    }

    public int getWaitTicks(){
        return waitTicks;
    }

    //setters
    public void setState(RobotState state){
        this.state = state;
    }

    public void setCurrentTask(Task currentTask){
        this.currentTask = currentTask;
    }

    public void setCarrying(boolean carrying){
        this.carrying = carrying;
    }

    public void setPath(ArrayList<Position> path){
        this.path = path;
    }

    //short summary, e.g. "R1 (1,1) 91% TO_PICKUP"
    @Override
    public String toString(){
        return id + " " + position + " " + battery.getPercentText() + " " + state.getLabel();
    }
}
