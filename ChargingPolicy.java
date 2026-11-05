package charging;

import exception.UnreachableDestinationException;
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

        if (robot == null || task == null || sim == null
                || robot.getBattery() == null) {
            return false;
        }

        Position robotPosition = robot.getPosition();
        Position pickup = task.getPickup();
        Position dropOff = task.getDropoff();

        if (robotPosition == null || pickup == null || dropOff == null) {
            return false;
        }

        ChargingStation charger = findNearestCharger(dropOff, sim);

        if (charger == null) {
            return false;
        }

        Position chargerPosition = charger.getPosition();

        int toPickup = pathDistance(robotPosition, pickup, sim);
        int toDropOff = pathDistance(pickup, dropOff, sim);
        int toCharger = pathDistance(dropOff, chargerPosition, sim);

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
        Position dropOff = task.getDropoff();

        if (pickup == null || dropOff == null) {
            return false;
        }

        for (ChargingStation startCharger : sim.getChargers()) {

            if (startCharger == null || startCharger.getPosition() == null) {
                continue;
            }

            int toPickup = pathDistance(
                    startCharger.getPosition(), pickup, sim);

            int toDropOff = pathDistance(pickup, dropOff, sim);

            if (toPickup < 0 || toDropOff < 0) {
                continue;
            }

            for (ChargingStation endCharger : sim.getChargers()) {

                if (endCharger == null || endCharger.getPosition() == null) {
                    continue;
                }

                int toEndCharger = pathDistance(
                        dropOff, endCharger.getPosition(), sim);

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

        if (robot == null || next == null || sim == null
                || robot.getBattery() == null) {
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

        int distance = pathDistance(
                next, charger.getPosition(), sim);

        if (distance < 0) {
            return true;
        }

        int requiredBattery = distance * MOVE_COST + RESERVE;

        return batteryAfterMove < requiredBattery;
    }

    //Finds the nearest charger by path length, null if none can be reached
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

            int distance = pathDistance(
                    from, charger.getPosition(), sim);

            if (distance >= 0 && distance < shortestDistance) {
                shortestDistance = distance;
                nearest = charger;
            }
        }

        return nearest;
    }

    //Finds the nearest charger the robot can safely reach
    //while keeping the required reserve
    public ChargingStation findReachableCharger(
            Position from, int batteryLevel, Simulation sim) {

        if (from == null || sim == null
                || batteryLevel < 0 || batteryLevel > MAX_BATTERY) {
            return null;
        }

        ChargingStation nearest = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (ChargingStation charger : sim.getChargers()) {

            if (charger == null || charger.getPosition() == null) {
                continue;
            }

            int distance = pathDistance(
                    from, charger.getPosition(), sim);

            if (distance < 0) {
                continue;
            }

            int requiredBattery = distance * MOVE_COST + RESERVE;

            if (requiredBattery <= batteryLevel
                    && distance < shortestDistance) {

                shortestDistance = distance;
                nearest = charger;
            }
        }

        return nearest;
    }

    //Calculates battery remaining after completing a task
    //and reaching the nearest charger
    //Returns -1 if the calculation cannot be completed
    public int getRemainingBatteryAfterTask(
            Robot robot, Task task, Simulation sim) {

        if (robot == null || task == null || sim == null
                || robot.getBattery() == null) {
            return -1;
        }

        Position robotPosition = robot.getPosition();
        Position pickup = task.getPickup();
        Position dropOff = task.getDropoff();

        if (robotPosition == null || pickup == null || dropOff == null) {
            return -1;
        }

        ChargingStation charger = findNearestCharger(dropOff, sim);

        if (charger == null) {
            return -1;
        }

        int toPickup = pathDistance(robotPosition, pickup, sim);
        int toDropOff = pathDistance(pickup, dropOff, sim);
        int toCharger = pathDistance(
                dropOff, charger.getPosition(), sim);

        if (toPickup < 0 || toDropOff < 0 || toCharger < 0) {
            return -1;
        }

        int totalDistance = toPickup + toDropOff + toCharger;
        int totalCost = totalDistance * MOVE_COST;

        return robot.getBattery().getLevel() - totalCost;
    }

    //Calculates how many simulation ticks are needed to reach a target battery level
    public int getChargingTicksNeeded(Robot robot, int targetLevel) {

        if (robot == null || robot.getBattery() == null) {
            return -1;
        }

        if (targetLevel < 0 || targetLevel > MAX_BATTERY) {
            return -1;
        }

        int currentBattery = robot.getBattery().getLevel();

        if (currentBattery < 0 || currentBattery > MAX_BATTERY) {
            return -1;
        }

        if (currentBattery >= targetLevel) {
            return 0;
        }

        int batteryNeeded = targetLevel - currentBattery;

        return (batteryNeeded + CHARGE_RATE - 1) / CHARGE_RATE;
    }

    //Robot can leave charging when it reaches RELEASE_LEVEL
    public boolean shouldReleaseFromCharger(Robot robot) {

        if (robot == null || robot.getBattery() == null) {
            return false;
        }

        return robot.getBattery().getLevel() >= RELEASE_LEVEL;
    }

    //Checks whether a robot can safely move to its next position
    public boolean canMoveSafely(
            Robot robot, Position next, Simulation sim) {

        if (robot == null || next == null || sim == null
                || robot.getBattery() == null) {
            return false;
        }

        return !mustGoCharge(robot, next, sim);
    }

    //Checks whether a robot is below the charging threshold
    public boolean needsCharging(Robot robot) {

        if (robot == null || robot.getBattery() == null) {
            return true;
        }

        int batteryLevel = robot.getBattery().getLevel();

        return batteryLevel < RELEASE_LEVEL;
    }

    //path length from the PathFinder, or -1 if there is no path
    //(the checks above treat anything below 0 as unreachable)
    private int pathDistance(Position from, Position to, Simulation sim) {

        try {
            return sim.getPathFinder().distance(
                    sim.getWarehouse(), from, to);

        } catch (UnreachableDestinationException e) {
            return -1;
        }
    }
}