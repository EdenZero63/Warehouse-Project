package robot;

import sim.Scenario;
import sim.Simulation;
import task.Task;
import testutil.Check;
import warehouse.Position;

//owner: Role 3 (robot behavior)
//tests for Robot and its states
public class RobotTest{
    public static void run(){
        Robot r = new Robot("R1", new Position(0, 0), 50);
        Check.isTrue(r.isIdle(), "a new robot starts idle");
        Check.equal("IDLE", r.getState().getLabel(), "idle label");
        Check.isTrue(!r.isCarrying(), "a new robot carries nothing");

        fullCycle();
        blockedThenReplans();
        carryingRobotKeepsItemWhenCharging();
        ruleBDivertsAndFinishes();
        strandedWithoutItem();
        strandedWithItem();
    }

    private static Simulation fresh(){
        Simulation sim = new Simulation(Scenario.createDefault());
        sim.reset();
        return sim;
    }

    //R1 is 2 cells from P1 and P1 is 7 cells from D1
    private static void fullCycle(){
        try{
            Simulation sim = fresh();
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            Robot r = sim.getRobot("R1");
            boolean sawPickup = false;
            boolean sawDropoff = false;
            for(int i = 0; i < 40 && !t.getStatus().equals(Task.COMPLETED); i++){
                sim.nextTick();
                sawPickup = sawPickup || r.getState() instanceof ToPickupState;
                sawDropoff = sawDropoff || r.getState() instanceof ToDropoffState;
            }
            Check.isTrue(sawPickup && sawDropoff, "task goes TO_PICKUP then TO_DROPOFF");
            Check.equal(Task.COMPLETED, t.getStatus(), "the task ends COMPLETED");
            Check.isTrue(r.isIdle() && !r.isCarrying(), "robot is idle and empty after delivering");
            Check.equal(new Position(3, 8), r.getPosition(), "robot ends on the drop-off");
            Check.isTrue(r.getBattery().getLevel() == 77.5, "9 moves use 22.5% battery");
        }catch(Exception e){
            Check.isTrue(false, "full cycle: " + e);
        }
    }

    //R2 parks in the way on the top row, the bottom row is the way around
    private static void blockedThenReplans(){
        try{
            Simulation sim = new Simulation(new Scenario("Block", new String[]{"C.P...D.C", "........."},
                new String[]{"R1", "R2", "R3"}, new int[][]{{0, 3}, {0, 4}, {1, 8}}, new int[]{100, 100, 100}));
            sim.reset();
            Robot r1 = sim.getRobot("R1");
            Robot r2 = sim.getRobot("R2");
            r2.setState(new WaitingForChargerState());
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            t.markAssigned(r1);
            t.markPickedUp();
            r1.setCarrying(true);

            Position start = r1.getPosition();
            for(int i = 0; i < 3; i++){
                sim.nextTick();
            }
            Check.equal(start, r1.getPosition(), "a blocked robot waits");
            Check.equal(3, r1.getWaitTicks(), "waits are counted");
            Check.isTrue(r1.getBattery().isFull(), "waiting uses no battery");

            boolean shared = false;
            for(int i = 0; i < 15 && !t.getStatus().equals(Task.COMPLETED); i++){
                sim.nextTick();
                shared = shared || r1.getPosition().equals(r2.getPosition());
            }
            Check.equal(Task.COMPLETED, t.getStatus(), "after 3 waits it re-plans around the blocker");
            Check.isTrue(!shared, "robots never share a cell");
        }catch(Exception e){
            Check.isTrue(false, "blocked robot: " + e);
        }
    }

    private static void carryingRobotKeepsItemWhenCharging(){
        try{
            Simulation sim = fresh();
            Robot r = sim.getRobot("R1");
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            t.markAssigned(r);
            t.markPickedUp();
            r.setCarrying(true);

            r.goCharge(sim);
            Check.isTrue(r.getState() instanceof ToChargerState, "goCharge heads to a charger");
            Check.isTrue(r.isCarrying() && r.getCurrentTask() == t, "the item and task stay with the robot");

            r.resumeAfterCharging(sim);
            Check.isTrue(r.getState() instanceof ToDropoffState, "after charging it resumes the delivery");
        }catch(Exception e){
            Check.isTrue(false, "carrying robot: " + e);
        }
    }

    //20% isn't enough for this trip, so it has to charge on the way and still finish
    private static void ruleBDivertsAndFinishes(){
        try{
            Simulation sim = fresh();
            Robot r = sim.getRobot("R1");
            Task t = sim.getTaskManager().createTask("P2", "D2", sim);
            r.getBattery().consume(r.getBattery().getLevel() - 20);
            t.markAssigned(r);
            boolean diverted = false;
            for(int i = 0; i < 300 && !t.getStatus().equals(Task.COMPLETED); i++){
                sim.nextTick();
                diverted = diverted || (r.getState() instanceof ToChargerState && r.getCurrentTask() == t);
            }
            Check.isTrue(diverted, "Rule B: a low robot goes to charge and keeps its task");
            Check.equal(Task.COMPLETED, t.getStatus(), "Rule B: it finishes the task after charging");
        }catch(Exception e){
            Check.isTrue(false, "Rule B run: " + e);
        }
    }

    private static void strandedWithoutItem(){
        try{
            Simulation sim = fresh();
            Robot r = sim.getRobot("R1");
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            t.markAssigned(r);
            r.getBattery().consume(100);
            r.strand(sim);
            Check.isTrue(r.getState() instanceof StrandedState, "battery 0 means STRANDED");
            Check.equal(Task.PENDING, t.getStatus(), "Rule D: an undelivered task goes back in line");
            Check.isTrue(r.getCurrentTask() == null, "a stranded robot lets go of its task");

            Position spot = r.getPosition();
            r.update(sim);
            Check.equal(spot, r.getPosition(), "a stranded robot doesn't move");
        }catch(Exception e){
            Check.isTrue(false, "stranded: " + e);
        }

        Simulation sim = fresh();
        Robot driving = new Robot("R9", new Position(3, 2), 0);
        driving.setState(new ToChargerState());
        driving.update(sim);
        Check.isTrue(driving.getState() instanceof StrandedState, "a robot heading to charge with 0 battery strands");
    }

    private static void strandedWithItem(){
        try{
            Simulation sim = fresh();
            Robot r = sim.getRobot("R1");
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            t.markAssigned(r);
            t.markPickedUp();
            r.setCarrying(true);
            r.strand(sim);
            Check.equal(Task.FAILED, t.getStatus(), "Rule D: a task with the item on board fails");
            Check.isTrue(!r.isCarrying(), "the lost item is cleared");
        }catch(Exception e){
            Check.isTrue(false, "stranded with item: " + e);
        }
    }
}
