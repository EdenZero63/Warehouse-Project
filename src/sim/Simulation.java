package sim;

import java.util.ArrayList;

import charging.ChargingPolicy;
import charging.ChargingStation;
import exception.RobotUnavailableException;
import navigation.BfsPathFinder;
import navigation.PathFinder;
import robot.Robot;
import task.Dispatcher;
import task.TaskManager;
import warehouse.Position;
import warehouse.Warehouse;

//owner: Role 1 (integration lead)
//holds the whole world and advances it one tick at a time (DESIGN.md section 5)
public class Simulation{
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
    public Simulation(Scenario scenario){
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
    public void reset(){
        //TODO (Role 1): warehouse = scenario.buildWarehouse(); robots = scenario.buildRobots();
        //chargers = scenario.buildChargers(); taskManager.clear(); log.clear(); tick = 0;
        throw new UnsupportedOperationException("TODO (Role 1): reset");
    }

    //advances one tick, always in this order so runs are reproducible:
    //  1. tick++
    //  2. dispatcher.assignTasks(this)
    //  3. robot.update(this) for each robot, in ID order
    //  4. charger.update(this) for each charger
    //  (states and chargers write to the log as things happen)
    public void nextTick(){
        //TODO (Role 1)
        throw new UnsupportedOperationException("TODO (Role 1): nextTick");
    }

    //find a robot by ID such as "R2", throws if missing
    public Robot getRobot(String id) throws RobotUnavailableException{
        //TODO (Role 1)
        throw new UnsupportedOperationException("TODO (Role 1): getRobot");
    }

    //menu "c" command (Rule D): recharge a STRANDED robot by hand so it can carry on
    //throws if the ID is unknown or the robot isn't stranded
    public void serviceRobot(String id) throws RobotUnavailableException{
        //TODO (Role 4): getRobot(id); if it isn't STRANDED throw RobotUnavailableException;
        //otherwise refill the battery and call resumeAfterCharging(this)
        throw new UnsupportedOperationException("TODO (Role 4): serviceRobot");
    }

    //true if any robot other than `ignore` is standing on `p`
    public boolean isOccupied(Position p, Robot ignore){
        //TODO (Role 1)
        throw new UnsupportedOperationException("TODO (Role 1): isOccupied");
    }

    //getters
    public Scenario getScenario(){
        return scenario;
    }

    public Warehouse getWarehouse(){
        return warehouse;
    }

    public ArrayList<Robot> getRobots(){
        return robots;
    }

    public ArrayList<ChargingStation> getChargers(){
        return chargers;
    }

    public PathFinder getPathFinder(){
        return pathFinder;
    }

    public ChargingPolicy getChargingPolicy(){
        return chargingPolicy;
    }

    public TaskManager getTaskManager(){
        return taskManager;
    }

    public Dispatcher getDispatcher(){
        return dispatcher;
    }

    public EventLog getLog(){
        return log;
    }

    public int getTick(){
        return tick;
    }
}
