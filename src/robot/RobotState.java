package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior)
//abstract: what a robot does on one tick; each subclass handles one state,
//so Robot.update() just calls state.act() instead of one long if/else chain
public abstract class RobotState{

    //this state's work for one tick: move, pick up, drop off, wait, or switch state
    public abstract void act(Robot robot, Simulation sim);

    //short label for the status panel, e.g. "TO_PICKUP"
    public abstract String getLabel();

    //prints as its label
    @Override
    public String toString(){
        return getLabel();
    }
}
