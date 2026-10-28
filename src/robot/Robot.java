package robot;

import java.util.ArrayList;

import charging.Battery;
import sim.Simulation;
import task.Task;
import warehouse.Position;

//owner: Role 3 (robot behavior)
//a robot's data; what it does each tick is decided by its current RobotState
public class Robot{
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
        //TODO (Role 3)
        throw new UnsupportedOperationException("TODO (Role 3): stepAlongPath");
    }

    //after charging: carry on with the saved task (drop-off if carrying, else pickup), or go idle
    public void resumeAfterCharging(Simulation sim){
        //TODO (Role 3)
        throw new UnsupportedOperationException("TODO (Role 3): resumeAfterCharging");
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
        return id + " " + position + " " + battery.getLevel() + "% " + state.getLabel();
    }
}
