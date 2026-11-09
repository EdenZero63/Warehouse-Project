package charging;

import robot.ChargingState;
import robot.Robot;
import robot.WaitingForChargerState;
import sim.Scenario;
import sim.Simulation;
import task.Task;
import testutil.Check;
import warehouse.Position;

//owner: Role 4 (charging)
//tests for Battery, ChargingPolicy and ChargingStation
public class ChargingPolicyTest{
    public static void run(){
        battery();
        ruleA();
        ruleB();
        nearestCharger();
        chargerQueue();
        releaseAtFullWhenNobodyWaits();
    }

    private static Simulation fresh(){
        Simulation sim = new Simulation(Scenario.createDefault());
        sim.reset();
        return sim;
    }

    //drains the battery down to exactly this level
    private static void setLevel(Robot r, double level){
        r.getBattery().consume(r.getBattery().getLevel() - level);
    }

    private static void battery(){
        Check.equal(25, (int) new Battery(25).getLevel(), "battery starts at the given level");
        Check.equal(100, (int) new Battery(150).getLevel(), "battery can't start above the max");
        Check.equal(0, (int) new Battery(-5).getLevel(), "battery can't start below 0");

        Battery b = new Battery(10);
        b.consume(50);
        Check.isTrue(b.isEmpty(), "battery never goes below 0");
        b.charge(500);
        Check.isTrue(b.isFull(), "battery never goes above the max");

        b.consumeTileMovement();
        Check.isTrue(b.getLevel() == ChargingPolicy.MAX_BATTERY - ChargingPolicy.MOVE_COST, "one tile costs MOVE_COST");
        Check.equal("97.5%", b.getPercentText(), "half percents show the decimal");
        Check.equal("25%", new Battery(25).getPercentText(), "whole percents have no .0");

        boolean threw = false;
        try{
            b.consume(-1);
        }catch(IllegalArgumentException e){
            threw = true;
        }
        Check.isTrue(threw, "negative amounts are rejected");
    }

    //R1 (3,3) -> P1 is 2 cells, P1 -> D1 is 7, D1 -> C2 is 2: 11 cells x 2.5 + RESERVE 6 = 33.5
    private static void ruleA(){
        try{
            Simulation sim = fresh();
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            Robot r = sim.getRobot("R1");
            ChargingPolicy policy = sim.getChargingPolicy();
            setLevel(r, 33.5);
            Check.isTrue(policy.canAcceptTask(r, t, sim), "Rule A: accepts with exactly enough battery");
            setLevel(r, 33);
            Check.isTrue(!policy.canAcceptTask(r, t, sim), "Rule A: refuses when just short");
            Check.isTrue(policy.isPossibleOnFullBattery(t, sim), "a normal task is possible on a full battery");
        }catch(Exception e){
            Check.isTrue(false, "Rule A setup: " + e);
        }

        //charger, pickup, then a corridor too long for one charge
        Simulation longMap = new Simulation(new Scenario("Long", new String[]{"CP.............................D"},
            new String[]{"R1", "R2", "R3"}, new int[][]{{0, 2}, {0, 3}, {0, 4}}, new int[]{100, 100, 100}));
        longMap.reset();
        Task tooLong = new Task(1, new Position(0, 1), new Position(0, 31));
        Check.isTrue(!longMap.getChargingPolicy().isPossibleOnFullBattery(tooLong, longMap), "Rule A: too long for a full battery");
    }

    //stepping onto D1 (3,8) leaves 2 cells to C2, so it needs 2 x 2.5 + 6 = 11 left after the step
    private static void ruleB(){
        try{
            Simulation sim = fresh();
            Robot r = sim.getRobot("R1");
            Position next = new Position(3, 8);
            setLevel(r, 13.5);
            Check.isTrue(!sim.getChargingPolicy().mustGoCharge(r, next, sim), "Rule B: keeps going when it can still get home");
            setLevel(r, 13);
            Check.isTrue(sim.getChargingPolicy().mustGoCharge(r, next, sim), "Rule B: turns back at the point of no return");
        }catch(Exception e){
            Check.isTrue(false, "Rule B setup: " + e);
        }
    }

    //C1 is closer in a straight line but behind a wall, C2 is closer to actually drive to
    private static void nearestCharger(){
        Simulation sim = new Simulation(new Scenario("Wall", new String[]{"C#....C", ".#.....", "......."},
            new String[]{"R1", "R2", "R3"}, new int[][]{{1, 3}, {1, 4}, {1, 5}}, new int[]{100, 100, 100}));
        sim.reset();
        ChargingStation nearest = sim.getChargingPolicy().findNearestCharger(new Position(0, 2), sim);
        Check.equal("C2", nearest.getId(), "nearest charger is by path, not straight line");
    }

    private static void chargerQueue(){
        try{
            Simulation sim = fresh();
            ChargingStation station = sim.getChargers().get(0);
            Robot r1 = sim.getRobot("R1");
            Robot r2 = sim.getRobot("R2");
            Robot r3 = sim.getRobot("R3");
            setLevel(r1, 80);

            station.arrive(r1);
            station.arrive(r2);
            station.arrive(r3);
            Check.isTrue(r1.getState() instanceof ChargingState, "first robot gets the charger");
            Check.isTrue(r2.getState() instanceof WaitingForChargerState, "second robot waits");
            Check.equal(2, station.getQueueSize(), "two robots are in line");

            station.update(sim);
            Check.isTrue(r1.getBattery().getLevel() == 95, "charger adds CHARGE_RATE");
            Check.isTrue(r1.isIdle(), "occupant leaves at RELEASE_LEVEL when someone is waiting");
            Check.isTrue(station.getOccupant() == r2, "the next robot in line takes the charger");
            Check.equal(1, station.getQueueSize(), "the line gets shorter");

            station.update(sim);
            Check.isTrue(station.getOccupant() == r3, "robots leave the line in arrival order");
        }catch(Exception e){
            Check.isTrue(false, "charger queue setup: " + e);
        }
    }

    private static void releaseAtFullWhenNobodyWaits(){
        try{
            Simulation sim = fresh();
            ChargingStation station = sim.getChargers().get(0);
            Robot r = sim.getRobot("R1");
            setLevel(r, 80);
            station.arrive(r);
            station.update(sim);
            Check.isTrue(station.getOccupant() == r, "stays past RELEASE_LEVEL when nobody is waiting");
            station.update(sim);
            Check.isTrue(station.isFree() && r.getBattery().isFull(), "leaves when full");
        }catch(Exception e){
            Check.isTrue(false, "release setup: " + e);
        }
    }
}
