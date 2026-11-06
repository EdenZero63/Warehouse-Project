package task;

import java.util.ArrayList;

import exception.InvalidTaskException;
import exception.UnreachableDestinationException;
import sim.Simulation;

//owner: Role 6 (tasks and dispatch)
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
    //rejects unknown labels, pickup = drop-off, unreachable cells, and tasks too long for a full battery
    public Task createTask(String pickupLabel, String dropoffLabel, Simulation sim)
            throws InvalidTaskException, UnreachableDestinationException{
        try {
            pickup = sim.getWarehouse().findLabel(pickupLabel);
            dropoff = sim.getWarehouse().findLabel(dropoffLabel);
            
            if (pickup.equals(dropoff)) { throw new InvalidPositionException("Pickup and dropoff are the same position"); }
            if (!sim.getWarehouse().isInBounds(pickup)) { throw new InvalidPositionException("Pickup out of bounds"); }
            if (!sim.getWarehouse().isInBounds(dropoff)) { throw new InvalidPositionException("Dropoff out of bounds"); }
            //TODO (Role 6): Warehouse.findLabel throws UnreachableDestinationException for unreachable cells and taks too long for a full battery;

            
            Task task = new Task(nextID, pickup, dropoff);
            this.nextID++;
            this.tasks.add(task);
            return task;
        }
        catch (InvalidPositionException e) {
            throw new InvalidTaskException("Invalid Position: " + e.getMessage);
        }
        throw new UnsupportedOperationException("TODO (Role 6): createTask");
    }

    //return a copy so outside code can't change the list
    public ArrayList<Task> getAll(){
        return new ArrayList<Task>(tasks);
    }

    //pending tasks, oldest first
    public ArrayList<Task> getPending(){
        ArrayList<Task> pending = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getStatus() == Task.PENDING) { pending.add(tasks.get(i)); }
        }
        return pending;
        throw new UnsupportedOperationException("TODO (Role 6): getPending");
    }

    //how many tasks have this status
    public int countByStatus(String status){
        ArrayList<Task> tasksOfStatus = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getStatus().equals()) { tasksOfStatus.add(tasks.get(i)); }
        }
        return tasksOfStatus;
        throw new UnsupportedOperationException("TODO (Role 6): countByStatus");
    }

    //used by reset: removes every task and restarts IDs at 1
    public void clear(){
        tasks.clear();
        nextId = 1;
    }
}
