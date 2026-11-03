
package charging;

import robot.Robot;
import sim.Simulation;
import task.Task;
import warehouse.Position;

//owner: Role 4 (charging)
//every battery number and rule lives here (DESIGN.md section 4)
//distances always come from sim.getPathFinder(), never straight-line
public class ChargingPolicy {

    //battery numbers
    public static final int MAX_BATTERY = 100;
    public static final int MOVE_COST = 5; // 1 tile moved = 5% battery
    public static final int WAIT_COST = 0;
    public static final int CHARGE_RATE = 15;
    public static final int RESERVE = 6;
    public static final int RELEASE_LEVEL = 80;

    //Rule A: robot -> pickup -> drop-off -> nearest charger
    //and still RESERVE left
    public boolean canAcceptTask(Robot robot, Task task, Simulation sim) {
        if (robot == null || task == null || sim == null) {
            return false;
        }

        Position robotPosition = robot.getPosition();
        Position pickup = task.getPickup();
        Position dropOff = task.getDropOff();

        if (robotPosition == null || pickup == null || dropOff == null) {
            return false;
        }

        ChargingStation charger = findNearestCharger(dropOff, sim);
        if (charger == null) {
            return false;
        }

        Position chargerPosition = charger.getPosition();

        int toPickup = sim.getPathFinder().distance(robotPosition, pickup);
        int toDropOff = sim.getPathFinder().distance(pickup, dropOff);
        int toCharger = sim.getPathFinder().distance(dropOff, chargerPosition);

        if (toPickup < 0 || toDropOff < 0 || toCharger < 0) {
            return false;
        }

        int totalCost = (toPickup + toDropOff + toCharger) * MOVE_COST;

        return robot.getBattery().getLevel() >= totalCost + RESERVE;
    }

    //Rule A, second half: could any robot do this task starting full at a charger?
    //If not, TaskManager rejects the task when it is created
    public boolean isPossibleOnFullBattery(Task task, Simulation sim) {
        if (task == null || sim == null) {
            return false;
        }

        Position pickup = task.getPickup();
        Position dropOff = task.getDropOff();

        if (pickup == null || dropOff == null) {
            return false;
        }

        for (ChargingStation startCharger : sim.getChargers()) {
            if (startCharger == null || startCharger.getPosition() == null) {
                continue;
            }

            int toPickup = sim.getPathFinder().distance(
                    startCharger.getPosition(), pickup);
            int toDropOff = sim.getPathFinder().distance(pickup, dropOff);

            if (toPickup < 0 || toDropOff < 0) {
                continue;
            }

            for (ChargingStation endCharger : sim.getChargers()) {
                if (endCharger == null || endCharger.getPosition() == null) {
                    continue;
                }

                int toEndCharger = sim.getPathFinder().distance(
                        dropOff, endCharger.getPosition());

                if (toEndCharger < 0) {
                    continue;
                }

                int totalCost = (toPickup + toDropOff + toEndCharger)
                        * MOVE_COST;

                if (totalCost + RESERVE <= MAX_BATTERY) {
                    return true;
                }
            }
        }

        return false;
    }

    //Rule B: would stepping to next leave too little to reach a charger + RESERVE?
    public boolean mustGoCharge(Robot robot, Position next, Simulation sim) {
        if (robot == null || next == null || sim == null) {
            return true;
        }

        int currentBattery = robot.getBattery().getLevel();
        int batteryAfterMove = currentBattery - MOVE_COST;

        if (batteryAfterMove < 0) {
            return true;
        }

        ChargingStation charger = findNearestCharger(next, sim);

        if (charger == null) {
            return true;
        }

        int distance = sim.getPathFinder().distance(
                next, charger.getPosition());

        if (distance < 0) {
            return true;
        }

        int requiredBattery = distance * MOVE_COST + RESERVE;

        return batteryAfterMove < requiredBattery;
    }

    //nearest charger by path length, null if none can be reached
    public ChargingStation findNearestCharger(Position from, Simulation sim) {
        if (from == null || sim == null) {
            return null;
        }

        ChargingStation nearest = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (ChargingStation charger : sim.getChargers()) {
            if (charger == null || charger.getPosition() == null) {
                continue;
            }

            int distance = sim.getPathFinder().distance(
                    from, charger.getPosition());

            if (distance >= 0 && distance < shortestDistance) {
                shortestDistance = distance;
                nearest = charger;
            }
        }

        return nearest;
    }
}