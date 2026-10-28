package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior), rules from Role 4
//driving to the nearest charger; the task (if any) is kept and resumed afterwards
public class ToChargerState extends RobotState{

    //step toward the charger, check in on arrival
    @Override
    public void act(Robot robot, Simulation sim){
        //TODO (Role 3):
        //  1. step toward sim.getChargingPolicy().findNearestCharger(...)
        //  2. battery hits 0 on the way -> StrandedState (Rule D)
        //  3. at the charger: station.arrive(robot), the station puts it in Charging or Waiting
        throw new UnsupportedOperationException("TODO (Role 3): ToChargerState.act");
    }

    @Override
    public String getLabel(){
        return "TO_CHARGER";
    }
}
