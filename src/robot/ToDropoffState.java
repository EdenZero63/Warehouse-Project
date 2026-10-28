package robot;

import sim.Simulation;

//owner: Role 3 (robot behavior)
//carrying an item to the task's drop-off point
public class ToDropoffState extends RobotState{

    //check battery, step once, deliver on arrival
    @Override
    public void act(Robot robot, Simulation sim){
        //TODO (Role 3):
        //  1. Rule B: if it must charge, switch to ToChargerState but KEEP the item and the task
        //  2. otherwise robot.stepAlongPath(sim)
        //  3. at the drop-off: setCarrying(false), task.markCompleted(), setCurrentTask(null),
        //     switch to IdleState, log "R1 delivered to D2"
        throw new UnsupportedOperationException("TODO (Role 3): ToDropoffState.act");
    }

    @Override
    public String getLabel(){
        return "TO_DROPOFF";
    }
}
