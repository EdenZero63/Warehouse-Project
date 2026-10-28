package task;

import sim.Simulation;

//owner: Role 6 (tasks and dispatch)
//hands pending tasks to idle robots once per tick (DESIGN.md section 5, step 2)
public class Dispatcher{

    //oldest pending task goes to the nearest idle robot that passes Rule A (ChargingPolicy.canAcceptTask)
    //ties go to the lower robot ID; an idle robot that fails Rule A is sent to charge, task stays PENDING
    public void assignTasks(Simulation sim){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): assignTasks");
    }
}
