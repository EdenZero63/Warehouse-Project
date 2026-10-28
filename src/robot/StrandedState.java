package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior), rules from Role 4
//battery hit 0 (Rule D); the robot stops, other robots treat its cell as a wall,
//and only the menu's "service robot" command brings it back
public class StrandedState extends RobotState{

    //does nothing until serviced
    @Override
    public void act(Robot robot, Simulation sim){
    }

    @Override
    public String getLabel(){
        return "STRANDED";
    }
}
