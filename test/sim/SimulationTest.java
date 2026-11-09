package sim;

import java.util.ArrayList;

import exception.RobotUnavailableException;
import robot.Robot;
import robot.StrandedState;
import robot.ToChargerState;
import task.Task;
import testutil.Check;

//owner: Role 1 (integration lead)
//tests for Simulation and Scenario
public class SimulationTest{
    public static void run(){
        Simulation sim = new Simulation(Scenario.createDefault());
        Check.equal(0, sim.getTick(), "a new simulation starts at tick 0");

        resetAndTick();
        unknownRobot();
        resetMidRun();
        reproducible();
        serviceRobot();
        everyTaskAtOnce();
    }

    private static Simulation fresh(){
        Simulation sim = new Simulation(Scenario.createDefault());
        sim.reset();
        return sim;
    }

    private static void resetAndTick(){
        Simulation sim = fresh();
        Check.equal(3, sim.getRobots().size(), "reset builds 3 robots");
        Check.equal(4, sim.getChargers().size(), "reset builds 4 chargers");
        Check.isTrue(sim.getTaskManager().getAll().isEmpty(), "reset starts with no tasks");
        Check.isTrue(!sim.hasWork(), "nothing to do with no tasks");
        sim.nextTick();
        Check.equal(1, sim.getTick(), "nextTick adds 1 to the tick");
    }

    private static void unknownRobot(){
        Simulation sim = fresh();
        boolean threw = false;
        try{
            sim.getRobot("R4");
        }catch(RobotUnavailableException e){
            threw = true;
        }
        Check.isTrue(threw, "an unknown robot ID throws");
    }

    private static void resetMidRun(){
        try{
            Simulation sim = fresh();
            sim.getTaskManager().createTask("P1", "D1", sim);
            for(int i = 0; i < 4; i++){
                sim.nextTick();
            }
            Check.isTrue(!"R1 (3,3) 100% IDLE".equals(sim.getRobot("R1").toString()), "the robot has moved before the reset");
            sim.reset();
            Check.equal(0, sim.getTick(), "reset goes back to tick 0");
            Check.isTrue(sim.getTaskManager().getAll().isEmpty(), "reset clears the tasks");
            Check.equal("R1 (3,3) 100% IDLE", sim.getRobot("R1").toString(), "reset puts the robot back");
        }catch(Exception e){
            Check.isTrue(false, "reset mid-run: " + e);
        }
    }

    //the whole state after a fixed list of commands
    private static String script() throws Exception{
        Simulation sim = fresh();
        sim.getTaskManager().createTask("P1", "D2", sim);
        sim.getTaskManager().createTask("P4", "D1", sim);
        sim.getTaskManager().createTask("P3", "D2", sim);
        for(int i = 0; i < 40; i++){
            sim.nextTick();
        }
        return sim.getRobots() + " " + sim.getTaskManager().getAll();
    }

    private static void reproducible(){
        try{
            Check.equal(script(), script(), "the same commands give the same run");
        }catch(Exception e){
            Check.isTrue(false, "reproducible: " + e);
        }
    }

    private static void serviceRobot(){
        try{
            Simulation sim = fresh();
            Robot r = sim.getRobot("R1");
            r.getBattery().consume(100);
            r.setState(new ToChargerState());
            sim.nextTick();
            Check.isTrue(r.getState() instanceof StrandedState, "the robot is stranded first");
            sim.serviceRobot("R1");
            Check.isTrue(r.isIdle() && r.getBattery().isFull(), "servicing recharges a stranded robot");

            boolean threw = false;
            try{
                sim.serviceRobot("R2");
            }catch(RobotUnavailableException e){
                threw = true;
            }
            Check.isTrue(threw, "only a stranded robot can be serviced");
        }catch(Exception e){
            Check.isTrue(false, "service robot: " + e);
        }
    }

    //all 8 pickup and drop-off pairs at once, robots have to share the map and the chargers
    private static void everyTaskAtOnce(){
        try{
            Simulation sim = fresh();
            String[] pickups = {"P1", "P2", "P3", "P4"};
            String[] dropoffs = {"D1", "D2"};
            for(int p = 0; p < pickups.length; p++){
                for(int d = 0; d < dropoffs.length; d++){
                    sim.getTaskManager().createTask(pickups[p], dropoffs[d], sim);
                }
            }

            boolean offMap = false;
            boolean shared = false;
            boolean stranded = false;
            for(int i = 0; i < 2000 && sim.hasWork(); i++){
                sim.nextTick();
                ArrayList<Robot> robots = sim.getRobots();
                for(int a = 0; a < robots.size(); a++){
                    offMap = offMap || !sim.getWarehouse().isWalkable(robots.get(a).getPosition());
                    stranded = stranded || robots.get(a).getState() instanceof StrandedState;
                    for(int b = a + 1; b < robots.size(); b++){
                        shared = shared || robots.get(a).getPosition().equals(robots.get(b).getPosition());
                    }
                }
            }
            Check.equal(8, sim.getTaskManager().countByStatus(Task.COMPLETED), "all 8 tasks get delivered");
            Check.isTrue(!offMap, "robots never leave the map or enter a rack");
            Check.isTrue(!shared, "robots never share a cell");
            Check.isTrue(!stranded, "no robot runs out of battery");
        }catch(Exception e){
            Check.isTrue(false, "every task at once: " + e);
        }
    }
}
