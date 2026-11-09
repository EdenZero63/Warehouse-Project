package task;

import exception.InvalidTaskException;
import exception.RobotUnavailableException;
import exception.UnreachableDestinationException;
import robot.Robot;
import robot.WaitingForChargerState;
import sim.Scenario;
import sim.Simulation;
import testutil.Check;
import warehouse.Position;

//owner: Role 6 (tasks and dispatch)
//tests for Task, TaskManager and Dispatcher
public class TaskManagerTest{
    public static void run(){
        Task t = new Task(1, new Position(0, 0), new Position(5, 9));
        Check.equal(Task.PENDING, t.getStatus(), "a new task is PENDING");

        ids();
        rejectedTasks();
        unreachableDestination();
        taskTooLong();
        statusCounts();
        dispatcherPicksNearest();
        dispatcherTieGoesToLowerId();
        oldestTaskFirst();
        noRobotAvailable();
    }

    private static Simulation fresh(){
        Simulation sim = new Simulation(Scenario.createDefault());
        sim.reset();
        return sim;
    }

    private static void ids(){
        try{
            Simulation sim = fresh();
            TaskManager tm = sim.getTaskManager();
            Check.equal(1, tm.createTask("P1", "D1", sim).getId(), "the first task is #1");
            Check.equal(2, tm.createTask("P2", "D2", sim).getId(), "the second task is #2");
            tm.clear();
            Check.equal(1, tm.createTask("P1", "D1", sim).getId(), "IDs restart after clear");
        }catch(Exception e){
            Check.isTrue(false, "task IDs: " + e);
        }
    }

    //true if creating this task is refused with InvalidTaskException
    private static boolean refused(String pickup, String dropoff){
        Simulation sim = fresh();
        try{
            sim.getTaskManager().createTask(pickup, dropoff, sim);
            return false;
        }catch(InvalidTaskException e){
            return sim.getTaskManager().getAll().isEmpty();
        }catch(UnreachableDestinationException e){
            return false;
        }
    }

    private static void rejectedTasks(){
        Check.isTrue(refused("P1", "P1"), "pickup same as drop-off is refused");
        Check.isTrue(refused("P9", "D1"), "an unknown pickup is refused");
        Check.isTrue(refused("P1", "D9"), "an unknown drop-off is refused");
        Check.isTrue(refused("D1", "D2"), "a drop-off can't be the pickup");
        Check.isTrue(refused("P1", "P2"), "a pickup can't be the drop-off");
        Check.isTrue(refused("C1", "D1"), "a charger can't be the pickup");
        Check.isTrue(refused("", "D1"), "a blank pickup is refused");

        Simulation sim = fresh();
        boolean threw = false;
        try{
            sim.getTaskManager().checkPickup("P1", sim);
            sim.getTaskManager().checkPickup("D1", sim);
        }catch(InvalidTaskException e){
            threw = true;
        }
        Check.isTrue(threw, "checkPickup accepts P1 and refuses D1");
    }

    //the drop-off is behind a full wall
    private static void unreachableDestination(){
        Simulation sim = new Simulation(new Scenario("Walled", new String[]{"P..#D", "...#.", "C..#C"},
            new String[]{"R1", "R2", "R3"}, new int[][]{{0, 1}, {1, 1}, {2, 1}}, new int[]{100, 100, 100}));
        sim.reset();
        boolean threw = false;
        try{
            sim.getTaskManager().createTask("P1", "D1", sim);
        }catch(UnreachableDestinationException e){
            threw = true;
        }catch(InvalidTaskException e){
            threw = false;
        }
        Check.isTrue(threw, "a walled-off destination is refused");
        Check.isTrue(sim.getTaskManager().getAll().isEmpty(), "a refused task isn't stored");
    }

    private static void taskTooLong(){
        Simulation sim = new Simulation(new Scenario("Long", new String[]{"CP.............................D"},
            new String[]{"R1", "R2", "R3"}, new int[][]{{0, 2}, {0, 3}, {0, 4}}, new int[]{100, 100, 100}));
        sim.reset();
        boolean threw = false;
        try{
            sim.getTaskManager().createTask("P1", "D1", sim);
        }catch(InvalidTaskException e){
            threw = true;
        }catch(UnreachableDestinationException e){
            threw = false;
        }
        Check.isTrue(threw, "a task too long for a full battery is refused");
    }

    private static void statusCounts(){
        try{
            Simulation sim = fresh();
            TaskManager tm = sim.getTaskManager();
            Task t = tm.createTask("P1", "D1", sim);
            tm.createTask("P2", "D2", sim);
            Check.equal(2, tm.countByStatus(Task.PENDING), "both tasks start pending");
            Check.equal(2, tm.getPending().size(), "getPending lists them");

            t.markAssigned(sim.getRobot("R1"));
            Check.equal(1, tm.countByStatus(Task.ASSIGNED), "one task is assigned");
            t.markPickedUp();
            Check.equal(1, tm.countByStatus(Task.PICKED_UP), "one task is picked up");
            t.markCompleted();
            Check.equal(1, tm.countByStatus(Task.COMPLETED), "one task is completed");
            Check.equal(1, tm.countByStatus(Task.PENDING), "the other is still pending");

            boolean threw = false;
            try{
                t.markAssigned(sim.getRobot("R2"));
            }catch(IllegalStateException e){
                threw = true;
            }
            Check.isTrue(threw, "a finished task can't be assigned again");
        }catch(Exception e){
            Check.isTrue(false, "status counts: " + e);
        }
    }

    private static void dispatcherPicksNearest(){
        try{
            Simulation sim = fresh();
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            sim.getRobot("R2").placeAt(new Position(3, 2));
            sim.getDispatcher().assignTasks(sim);
            Check.equal("R2", t.getAssignedRobot().getId(), "the nearest robot gets the task");
        }catch(Exception e){
            Check.isTrue(false, "nearest robot: " + e);
        }
    }

    //R1 and R2 are both 1 cell from P1
    private static void dispatcherTieGoesToLowerId(){
        try{
            Simulation sim = fresh();
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            sim.getRobot("R1").placeAt(new Position(3, 0));
            sim.getRobot("R2").placeAt(new Position(3, 2));
            sim.getDispatcher().assignTasks(sim);
            Check.equal("R1", t.getAssignedRobot().getId(), "a tie goes to the lower robot ID");
        }catch(Exception e){
            Check.isTrue(false, "tie break: " + e);
        }
    }

    private static void oldestTaskFirst(){
        try{
            Simulation sim = fresh();
            Task first = sim.getTaskManager().createTask("P1", "D1", sim);
            Task second = sim.getTaskManager().createTask("P2", "D2", sim);
            sim.getDispatcher().assignTasks(sim);
            Check.equal(Task.ASSIGNED, first.getStatus(), "the oldest task is assigned first");
            Check.equal(Task.PENDING, second.getStatus(), "only one task is assigned per tick");
        }catch(Exception e){
            Check.isTrue(false, "oldest first: " + e);
        }
    }

    //every robot is busy, so nobody is available
    private static void noRobotAvailable(){
        try{
            Simulation sim = fresh();
            Task t = sim.getTaskManager().createTask("P1", "D1", sim);
            for(int i = 0; i < sim.getRobots().size(); i++){
                Robot r = sim.getRobots().get(i);
                r.setState(new WaitingForChargerState());
            }
            boolean threw = false;
            try{
                Dispatcher.getClosestIdleRobot(t, sim);
            }catch(RobotUnavailableException e){
                threw = true;
            }
            Check.isTrue(threw, "no idle robot means RobotUnavailableException");

            sim.getDispatcher().assignTasks(sim);
            Check.equal(Task.PENDING, t.getStatus(), "the task waits when no robot is available");
        }catch(Exception e){
            Check.isTrue(false, "no robot available: " + e);
        }
    }
}
