package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior), rules from Role 4
//sitting on a charger
public class ChargingState extends RobotState{

    //stays put; ChargingStation.update() adds the charge and releases the robot
    @Override
    public void act(Robot robot, Simulation sim){
    }

    @Override
    public String getLabel(){
        return "CHARGING";
    }
}
