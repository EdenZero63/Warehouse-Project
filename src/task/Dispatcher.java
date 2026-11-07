package task;

import charging.ChargingPolicy;
import exception.RobotUnavailableException;
import exception.UnreachableDestinationException;
import robot.Robot;
import robot.ToChargerState;
import sim.Simulation;

//owner: Role 6 (tasks and dispatch): James Whiting
//hands pending tasks to idle robots once per tick (DESIGN.md section 5, step 2)
public class Dispatcher{

    //oldest pending task goes to the nearest idle robot that passes Rule A (ChargingPolicy.canAcceptTask)
    //ties go to the lower robot ID; an idle robot that fails Rule A is sent to charge, task stays PENDING
    public void assignTasks(Simulation sim){
        Task task = sim.getTaskManager().getOldestPending();
        if (task == null) { return; } // nothing waiting this tick

        try {
            Robot robot = getClosestIdleRobot(task, sim);
            task.markAssigned(robot);
            sim.getLog().add(sim.getTick(), robot.getId() + " assigned task #" + task.getId());
            // Task has been assigned. Call again to assign another task.
        }
        catch (RobotUnavailableException e) {
            // No idle robot can take it right now, so the task stays PENDING (DESIGN.md section 6)
        }
    }

    // checks all robots: if a robot is idle, passes Rule A, and ((is closer or (equadistant and has a lower id) than the current closest) or there is no current closest), updates current closest.
    //                    if a robot is idle but doesn't pass Rule A, sends that robot to charge.
    // if no robot is found that is idle and passes Rule A, throws a RobotUnavailableException. Otherwise returns the closest robot.
    public static Robot getClosestIdleRobot(Task task, Simulation sim)
            throws RobotUnavailableException{
        Robot closest = null;
        int closestDistance = 0;
        for (int i = 0; i < sim.getRobots().size(); i++) {
            Robot robot = sim.getRobots().get(i);
            if (robot.isIdle()) {
                if (sim.getChargingPolicy().canAcceptTask(robot, task, sim)) {
                    int robotDistance;
                    try {
                        robotDistance = sim.getPathFinder().distance(sim.getWarehouse(), robot.getPosition(), task.getPickup());
                    }
                    catch (UnreachableDestinationException e) {
                        continue; // can't reach the pickup from here
                    }
                    if (closest == null) {
                        closest = robot;
                        closestDistance = robotDistance;
                        continue;
                    }
                    // Is the current robot closer?
                    if (robotDistance < closestDistance) { closest = robot; closestDistance = robotDistance; }
                    else if ((robotDistance == closestDistance)&&(robot.getId().compareTo(closest.getId()) < 0)) { closest = robot; closestDistance = robotDistance; }
                }
                else if (robot.getBattery().getLevel() < ChargingPolicy.RELEASE_LEVEL) {
                    // Only charge below RELEASE_LEVEL, otherwise a full robot bounces on and off the charger
                    robot.setState(new ToChargerState());
                }
            }
        }
        if (closest != null) { return closest; }
        throw new RobotUnavailableException("No Idle Robots");
    }
}
