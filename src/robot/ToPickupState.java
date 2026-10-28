package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior)
//driving to the task's pickup point
public class ToPickupState extends RobotState{

    //check battery, step once, pick up on arrival
    @Override
    public void act(Robot robot, Simulation sim){
        //TODO (Role 3):
        //  1. Rule B: if sim.getChargingPolicy().mustGoCharge(...), switch to ToChargerState
        //  2. otherwise robot.stepAlongPath(sim)
        //  3. at the pickup: setCarrying(true), task.markPickedUp(), plan the path to the drop-off,
        //     switch to ToDropoffState, log "R1 picked up at P1"
        throw new UnsupportedOperationException("TODO (Role 3): ToPickupState.act");
    }

    @Override
    public String getLabel(){
        return "TO_PICKUP";
    }
}
