package sim;

import java.util.ArrayList;

import charging.ChargingPolicy;
import charging.ChargingStation;
import exception.RobotUnavailableException;
import navigation.BfsPathFinder;
import navigation.PathFinder;
import robot.Robot;
import robot.StrandedState;
import task.Dispatcher;
import task.TaskManager;
import warehouse.Position;
import warehouse.Warehouse;

//owner: Role 1 (integration lead), serviced & battery handling by Role 7
//holds the whole world and advances it one tick at a time (DESIGN.md section 5)
public class Simulation {
    //fields
    private final Scenario scenario;
    private final PathFinder pathFinder;
    private final ChargingPolicy chargingPolicy;
    private final TaskManager taskManager;
    private final Dispatcher dispatcher;
    private final EventLog log;
    private Warehouse warehouse;
    private ArrayList<Robot> robots;
    private ArrayList<ChargingStation> chargers;
    private int tick;

    //constructor, reset() fills in the warehouse, robots and chargers (the menu calls it at startup)
    public Simulation(Scenario scenario) {
        this.scenario = scenario;
        this.pathFinder = new BfsPathFinder();
        this.chargingPolicy = new ChargingPolicy();
        this.taskManager = new TaskManager();
        this.dispatcher = new Dispatcher();
        this.log = new EventLog();
        this.warehouse = null;
        this.robots = new ArrayList<Robot>();
        this.chargers = new ArrayList<ChargingStation>();
        this.tick = 0;
    }

    //rebuilds everything from the scenario and goes back to tick 0
    public void reset() {
        warehouse = scenario.buildWarehouse();
        robots = scenario.buildRobots();
        chargers = scenario.buildChargers();
        taskManager.clear();
        log.clear();
        tick = 0;
        log.add(tick, scenario.getName() + " warehouse loaded: " + robots.size() + " robots, " + chargers.size() + " chargers");
    }

    //advances one tick, always in this order so runs are reproducible:
    //  1. tick++
    //  2. dispatcher.assignTasks(this)
    //  3. robot.update(this) for each robot, in ID order
    //  4. charger.update(this) for each charger
    //  (states and chargers write to the log as things happen)
    public void nextTick() {
        tick++;

        // 1. Assign pending tasks to idle robots via dispatcher
        try {
            dispatcher.assignTasks(this);
        } catch (UnsupportedOperationException e) {
            // Role 6 may still be implementing assignTasks
        }

        // 2. Update each robot in ID order (R1, R2, R3...)
        for (int i = 0; i < robots.size(); i++) {
            try {
                robots.get(i).update(this);
            } catch (UnsupportedOperationException e) {
                // Allows running even if certain robot sub-states are pending
            }
        }

        // 3. Update each charging station (charges occupants, manages queue)
        for (int i = 0; i < chargers.size(); i++) {
            chargers.get(i).update(this);
        }
    }

    //true while something can still happen without the user, the menu keeps ticking until this is false
    public boolean hasWork() {
        boolean anyRobotWorking = false;
        for (int i = 0; i < robots.size(); i++) {
            Robot r = robots.get(i);
            if (r.getState() instanceof StrandedState) {
                continue;
            }
            anyRobotWorking = true;
            if (!r.isIdle()) {
                return true;
            }
        }
        return anyRobotWorking && taskManager.getOldestPending() != null;
    }

    //find a robot by ID such as "R2", throws if missing
    public Robot getRobot(String id) throws RobotUnavailableException {
        if (id == null) {
            throw new RobotUnavailableException("Robot ID cannot be null.");
        }
        for (int i = 0; i < robots.size(); i++) {
            Robot r = robots.get(i);
            if (r.getId().equalsIgnoreCase(id.trim())) {
                return r;
            }
        }
        throw new RobotUnavailableException("No robot found with ID: " + id);
    }

    //Rule D: recharge a STRANDED robot by hand so it can carry on, there's no menu command for this right now
    //throws if the ID is unknown or the robot isn't stranded
    public void serviceRobot(String id) throws RobotUnavailableException {
        // 1. Locate the robot
        Robot robot = getRobot(id);

        // 2. Validate it is actually stranded (Rule D)
        if (!(robot.getState() instanceof StrandedState)) {
            throw new RobotUnavailableException("Robot " + id + " is not stranded (current state: " + robot.getState().getLabel() + ").");
        }

        // 3. Refill the battery back to full capacity (100%)
        robot.getBattery().charge(ChargingPolicy.MAX_BATTERY);

        // 4. Resume the robot so it can carry on
        robot.resumeAfterCharging(this);
        log.add(tick, id + " serviced and fully recharged");
    }

    //true if any robot other than `ignore` is standing on `p`
    public boolean isOccupied(Position p, Robot ignore) {
        if (p == null) {
            return false;
        }
        for (int i = 0; i < robots.size(); i++) {
            Robot r = robots.get(i);
            if (r != ignore && p.equals(r.getPosition())) {
                return true;
            }
        }
        return false;
    }

    //getters
    public Scenario getScenario() {
        return scenario;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public ArrayList<ChargingStation> getChargers() {
        return chargers;
    }

    public PathFinder getPathFinder() {
        return pathFinder;
    }

    public ChargingPolicy getChargingPolicy() {
        return chargingPolicy;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public EventLog getLog() {
        return log;
    }

    public int getTick() {
        return tick;
    }
}