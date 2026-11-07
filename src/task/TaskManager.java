package task;

import java.util.ArrayList;

import exception.InvalidPositionException;
import exception.InvalidTaskException;
import exception.UnreachableDestinationException;
import sim.Simulation;
import warehouse.Cell;
import warehouse.Position;

//owner: Role 6 (tasks and dispatch): James Whiting
//creates, stores and counts tasks
public class TaskManager{
    //fields
    private final ArrayList<Task> tasks;
    private int nextId;

    //constructor, IDs start at 1
    public TaskManager(){
        this.tasks = new ArrayList<Task>();
        this.nextId = 1;
    }

    //builds a task from labels like "P1" and "D2"
    //rejects unknown labels, a pickup that isn't a P or a drop-off that isn't a D, unreachable cells, and tasks too long for a full battery
    public Task createTask(String pickupLabel, String dropoffLabel, Simulation sim)
            throws InvalidTaskException, UnreachableDestinationException{
        try {
            Position pickup = sim.getWarehouse().findLabel(pickupLabel);
            Position dropoff = sim.getWarehouse().findLabel(dropoffLabel);
            if (sim.getWarehouse().getCell(pickup).getType() != Cell.PICKUP) { throw new InvalidTaskException(pickupLabel + " isn't a pickup point, use P1, P2..."); }
            if (sim.getWarehouse().getCell(dropoff).getType() != Cell.DROPOFF) { throw new InvalidTaskException(dropoffLabel + " isn't a drop-off point, use D1, D2..."); }
            
            if (pickup.equals(dropoff)) { throw new InvalidPositionException("Pickup and dropoff are the same position"); }
            if (!sim.getWarehouse().isInBounds(pickup)) { throw new InvalidPositionException("Pickup out of bounds"); }
            if (!sim.getWarehouse().isInBounds(dropoff)) { throw new InvalidPositionException("Dropoff out of bounds"); }
            // Walled off: distance throws UnreachableDestinationException (DESIGN.md section 6)
            sim.getPathFinder().distance(sim.getWarehouse(), pickup, dropoff);
            Task task = new Task(nextId, pickupLabel, pickup, dropoffLabel, dropoff);
            // Too long for a full battery is an InvalidTaskException (Rule A, DESIGN.md section 6)
            if (!sim.getChargingPolicy().isPossibleOnFullBattery(task, sim)) { throw new InvalidTaskException("Task is too long for a full battery"); }
            
            this.nextId++;
            this.tasks.add(task);
            return task;
        }
        catch (InvalidPositionException e) {
            throw new InvalidTaskException("Invalid Position: " + e.getMessage());
        }
    }

    //return a copy so outside code can't change the list
    public ArrayList<Task> getAll(){
        return new ArrayList<Task>(tasks);
    }

    //pending tasks, oldest first
    public ArrayList<Task> getPending(){
        ArrayList<Task> pending = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getStatus().equals(Task.PENDING)) { pending.add(tasks.get(i)); }
        }
        return pending;
    }

    //oldest pending task, null if nothing is waiting
    public Task getOldestPending(){
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getStatus().equals(Task.PENDING)) { return tasks.get(i); }
        }
        return null;
    }

    //how many tasks have this status
    public int countByStatus(String status){
        int count = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getStatus().equals(status)) { count++; }
        }
        return count;
    }

    //used by reset: removes every task and restarts IDs at 1
    public void clear(){
        tasks.clear();
        nextId = 1;
    }
}
