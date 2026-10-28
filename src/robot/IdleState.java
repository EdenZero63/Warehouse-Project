package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior)
//no job; the Dispatcher moves the robot out of this state by assigning a task
public class IdleState extends RobotState{

    //nothing to do while waiting for the Dispatcher
    @Override
    public void act(Robot robot, Simulation sim){
        //TODO (Role 3, optional): if no tasks are pending and battery isn't full, go top up
    }

    @Override
    public String getLabel(){
        return "IDLE";
    }
}
