package robot;

import java.util.ArrayList;

import sim.Simulation;
import task.Task;
import warehouse.Position;

//owner: Role 3 (robot behavior)
//driving to the task's pickup point
public class ToPickupState extends RobotState{

    //check battery, step once, pick up on arrival
    @Override
    public void act(Robot robot, Simulation sim){
        Task task = robot.getCurrentTask();
        if(driveTo(robot, task.getPickup(), sim)){
            robot.setCarrying(true);
            task.markPickedUp(); //this also switches the robot to ToDropoffState
            robot.setPath(new ArrayList<Position>());
            sim.getLog().add(sim.getTick(), robot.getId() + " picked up at " + task.getPickupLabel());
        }
    }

    @Override
    public String getLabel(){
        return "TO_PICKUP";
    }
}
