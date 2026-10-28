package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior), rules from Role 4
//in line for a busy charger, costs no battery (WAIT_COST = 0)
public class WaitingForChargerState extends RobotState{

    //stays put; ChargingStation.update() moves it to ChargingState when its turn comes
    @Override
    public void act(Robot robot, Simulation sim){
    }

    @Override
    public String getLabel(){
        return "WAITING_CHARGER";
    }
}
