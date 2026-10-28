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
        //TODO (Role 6): Warehouse.findLabel throws InvalidPositionException for unknown labels;
        //catch it and throw InvalidTaskException with a clear message instead
        throw new UnsupportedOperationException("TODO (Role 6): createTask");
    }

    //return a copy so outside code can't change the list
    public ArrayList<Task> getAll(){
        return new ArrayList<Task>(tasks);
    }

    //pending tasks, oldest first
    public ArrayList<Task> getPending(){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): getPending");
    }

    //how many tasks have this status
    public int countByStatus(String status){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): countByStatus");
    }

    //used by reset: removes every task and restarts IDs at 1
    public void clear(){
        tasks.clear();
        nextId = 1;
    }
}
