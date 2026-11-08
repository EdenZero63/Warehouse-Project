package robot;

import sim.Simulation;
import warehouse.Position;

//owner: Role 3 (robot behavior)
//abstract: what a robot does on one tick; each subclass handles one state,
//so Robot.update() just calls state.act() instead of one long if/else chain
public abstract class RobotState{

    //this state's work for one tick: move, pick up, drop off, wait, or switch state
    public abstract void act(Robot robot, Simulation sim);

    //shared by the two task states: checks Rule B, then moves one cell toward target
    //returns true once the robot is standing on target
    protected boolean driveTo(Robot robot, Position target, Simulation sim){
        if(robot.getPosition().equals(target)){
            return true;
        }
        if(!robot.prepareRoute(target, sim)){
            return false;
        }
        if(sim.getChargingPolicy().mustGoCharge(robot, robot.getPath().get(0), sim)){
            robot.goCharge(sim);
            return false;
        }
        robot.stepAlongPath(sim);
        return robot.getPosition().equals(target);
    }

    //short label for the status panel, e.g. "TO_PICKUP"
    public abstract String getLabel();

    //prints as its label
    @Override
    public String toString(){
        return getLabel();
    }
}
