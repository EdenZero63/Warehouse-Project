package robot;

import java.util.ArrayList;

import sim.Simulation;
import task.Task;
import warehouse.Position;

//owner: Role 3 (robot behavior)
//carrying an item to the task's drop-off point
public class ToDropoffState extends RobotState{

    //check battery, step once, deliver on arrival
    @Override
    public void act(Robot robot, Simulation sim){
        Task task = robot.getCurrentTask();
        if(driveTo(robot, task.getDropoff(), sim)){
            robot.setCarrying(false);
            task.markCompleted(); //this also clears the task and switches the robot to IdleState
            robot.setPath(new ArrayList<Position>());
            sim.getLog().add(sim.getTick(), robot.getId() + " delivered to " + task.getDropoffLabel());
        }
    }

    @Override
    public String getLabel(){
        return "TO_DROPOFF";
    }
}
