
package charging;

import java.util.ArrayList;

import robot.Robot;
import sim.Simulation;
import warehouse.Position;

//owner: Role 4 (charging)
//one charger cell; holds one robot at a time, the rest wait in arrival order (Rule C)
public class ChargingStation {

    //fields
    private final String id; //"C1"
    private final Position position;
    private Robot occupant; //null when free
    private final ArrayList<Robot> waiting;

    //constructor, starts free with an empty line
    public ChargingStation(String id, Position position) {
        this.id = id;
        this.position = position;
        this.occupant = null;
        this.waiting = new ArrayList<Robot>();
    }

    //getters
    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Robot getOccupant() {
        return occupant;
    }

    //free and queue checks
    public boolean isFree() {
        return occupant == null;
    }

    public int getQueueSize() {
        return waiting.size();
    }

    //robot reached the charger: take the spot if free,
    //otherwise join the line
    public void arrive(Robot robot) {
        if (robot == null) {
            return;
        }

        // Avoid adding the same robot more than once
        if (occupant == robot || waiting.contains(robot)) {
            return;
        }

        if (isFree()) {
            occupant = robot;
        } else {
            waiting.add(robot);
        }
    }

    //once per tick: charge the occupant, release it at MAX_BATTERY
    //(or RELEASE_LEVEL if someone is waiting), then resume and admit next robot
    public void update(Simulation sim) {
        if (occupant == null) {
            // If the charger is free, admit the next robot in line
            if (!waiting.isEmpty()) {
                occupant = waiting.remove(0);
            } else {
                return;
            }
        }

        // Charge the current occupant
        occupant.getBattery().charge(ChargingPolicy.CHARGE_RATE);

        int batteryLevel = occupant.getBattery().getLevel();

        // Release at full battery, or RELEASE_LEVEL if someone is waiting
        boolean shouldRelease;

        if (!waiting.isEmpty()) {
            shouldRelease = batteryLevel >= ChargingPolicy.RELEASE_LEVEL;
        } else {
            shouldRelease = batteryLevel >= ChargingPolicy.MAX_BATTERY;
        }

        if (shouldRelease) {
            Robot chargedRobot = occupant;
            occupant = null;

            // Resume the robot after charging
            chargedRobot.resumeAfterCharging(sim);

            // Let the next robot in the queue take the charger
            if (!waiting.isEmpty()) {
                occupant = waiting.remove(0);
            }
        }
    }
}