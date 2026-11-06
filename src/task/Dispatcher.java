package task;

import sim.Simulation;

//owner: Role 6 (tasks and dispatch): James Whiting
//hands pending tasks to idle robots once per tick (DESIGN.md section 5, step 2)
public class Dispatcher{

    //oldest pending task goes to the nearest idle robot that passes Rule A (ChargingPolicy.canAcceptTask)
    //ties go to the lower robot ID; an idle robot that fails Rule A is sent to charge, task stays PENDING
    public void assignTasks(Simulation sim){
        Task task = sim.getTaskManager().getOldestPending();

        try {
            Robot robot = this.getClosestIdleRobot(task, sim);
            task.markAssigned(robot);
            // Task has been assigned. Call again to assign another task.
        }
        catch (RobotUnavailableException e) {
            System.err.println(e.getMessage);
        }
    }

    // checks all robots: if a robot is idle, passes Rule A, and ((is closer or (equadistant and has a lower id) than the current closest) or there is no current closest), updates current closest.
    //                    if a robot is idle but doesn't pass Rule A, sends that robot to charge.
    // if no robot is found that is idle and passes Rule A, throws a RobotUnavailableException. Otherwise returns the closest robot.
    public static Robot getClosestIdleRobot(Task task, Simulation sim)
            throws RobotUnavailableException{
        Robot closest = null;
        for (int i = 0; i < sim.getRobots().size(); i++) {
            Robot robot = sim.getRobots().get(i);
            if (robot.isIdle()) {
                if (ChargingPolicy.canAcceptTask(robot, task, sim)) {
                    if (closest == null) { closest = robot; }
                    // Is the current robot closer?
                    robotDistance = ChargingPolicy.pathDistance(robot.getPosition(), task.getPickup(), sim));
                    closestDistance = ChargingPolicy.pathDistance(closest.getPosition(), task.getPickup(), sim))
                    else if (robotDistance < closestDistance) { closest = robot; }
                    else if ((robotDistance == closestDistance)&&(robot.getId() < closest.getID())) { closest = robot; }
                }
                else {
                    //TODO (Role 6): Update with the proper function call to send a robot to charge
                    robot.goCharge();
                }
            }
        }
        if (closest != null) { return closest; }
        throw new RobotUnavailableException("No Idle Robots");
    }
}
