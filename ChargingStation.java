
package charging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import robot.Robot;
import sim.Simulation;
import warehouse.Position;

// Owner: Role 4 (Charging)
// One charger cell; one robot charges at a time.
// Waiting robots are normally served in FIFO order.
public class ChargingStation {

    // Fields
    private final String id;
    private final Position position;

    private Robot occupant;
    private final ArrayList<Robot> waiting;

    // Statistics
    private int totalCharged;
    private int totalChargingTicks;
    private int totalWaitingTicks;

    // Track individual waiting and charging times
    private final Map<Robot, Integer> waitingTimes;
    private final Map<Robot, Integer> chargingTimes;

    // Constructor
    public ChargingStation(String id, Position position) {
        this.id = id;
        this.position = position;
        this.occupant = null;
        this.waiting = new ArrayList<>();

        this.totalCharged = 0;
        this.totalChargingTicks = 0;
        this.totalWaitingTicks = 0;

        this.waitingTimes = new HashMap<>();
        this.chargingTimes = new HashMap<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Robot getOccupant() {
        return occupant;
    }

    public int getQueueSize() {
        return waiting.size();
    }

    public int getTotalCharged() {
        return totalCharged;
    }

    public int getTotalChargingTicks() {
        return totalChargingTicks;
    }

    public int getTotalWaitingTicks() {
        return totalWaitingTicks;
    }

    // Station checks
    public boolean isFree() {
        return occupant == null;
    }

    public boolean hasWaitingRobots() {
        return !waiting.isEmpty();
    }

    public boolean isOccupied() {
        return occupant != null;
    }

    // Robot arrival
    public void arrive(Robot robot) {
        if (robot == null) {
            return;
        }

        // Prevent duplicate entries
        if (occupant == robot || waiting.contains(robot)) {
            return;
        }

        if (isFree()) {
            occupant = robot;
            chargingTimes.put(robot, 0);
        } else {
            waiting.add(robot);
            waitingTimes.put(robot, 0);
        }
    }

    // Remove a robot from the waiting queue
    public boolean removeWaitingRobot(Robot robot) {
        if (waiting.remove(robot)) {
            waitingTimes.remove(robot);
            return true;
        }

        return false;
    }

    // Peek at the next robot without removing it
    public Robot getNextWaitingRobot() {
        if (waiting.isEmpty()) {
            return null;
        }

        return waiting.get(0);
    }

    // Update station once per simulation tick
    public void update(Simulation sim) {

        // Update waiting times
        for (Robot robot : waiting) {
            waitingTimes.put(
                robot,
                waitingTimes.getOrDefault(robot, 0) + 1
            );

            totalWaitingTicks++;
        }

        // Admit next robot if station is free
        if (occupant == null) {
            admitNextRobot();
        }

        if (occupant == null) {
            return;
        }

        // Charge occupant
        occupant.getBattery().charge(
            ChargingPolicy.CHARGE_RATE
        );

        chargingTimes.put(
            occupant,
            chargingTimes.getOrDefault(occupant, 0) + 1
        );

        totalChargingTicks++;

        int batteryLevel = occupant.getBattery().getLevel();

        boolean shouldRelease;

        if (hasWaitingRobots()) {
            shouldRelease =
                batteryLevel >= ChargingPolicy.RELEASE_LEVEL;
        } else {
            shouldRelease =
                batteryLevel >= ChargingPolicy.MAX_BATTERY;
        }

        if (shouldRelease) {
            releaseRobot(sim);
        }
    }

    // Admit the next robot using FIFO
    private void admitNextRobot() {
        if (waiting.isEmpty()) {
            return;
        }

        occupant = waiting.remove(0);

        waitingTimes.remove(occupant);
        chargingTimes.put(occupant, 0);
    }

    // Release charged robot
    private void releaseRobot(Simulation sim) {

        Robot chargedRobot = occupant;

        occupant = null;

        totalCharged++;

        // Resume robot
        chargedRobot.resumeAfterCharging(sim);

        // Admit next robot
        admitNextRobot();
    }

    // Get individual robot waiting time
    public int getWaitingTime(Robot robot) {
        return waitingTimes.getOrDefault(robot, 0);
    }

    // Get individual robot charging time
    public int getChargingTime(Robot robot) {
        return chargingTimes.getOrDefault(robot, 0);
    }

    // Current station status
    public String getStatus() {

        if (occupant == null) {
            return waiting.isEmpty()
                ? "FREE"
                : "FREE - " + waiting.size() + " robot(s) waiting";
        }

        return "OCCUPIED by Robot | Queue: "
            + waiting.size();
    }

    // Display station statistics
    public void printStatistics() {

        System.out.println("===== CHARGING STATION " + id + " =====");

        System.out.println("Status: " + getStatus());

        System.out.println("Total Robots Charged: " + totalCharged);

        System.out.println("Total Charging Ticks: "
            + totalChargingTicks);

        System.out.println("Total Waiting Ticks: "
            + totalWaitingTicks);

        System.out.println("Current Queue Size: "
            + waiting.size());

        System.out.println("=================================");
    }
}
