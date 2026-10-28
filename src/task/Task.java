package task;

import robot.Robot;
import warehouse.Position;

//owner: Role 6 (tasks and dispatch)
//one pickup-and-delivery job: PENDING -> ASSIGNED -> PICKED_UP -> COMPLETED (or FAILED)
//statuses are String constants for now, switch to an enum if the class has covered enums
public class Task{
    //statuses
    public static final String PENDING = "PENDING";
    public static final String ASSIGNED = "ASSIGNED";
    public static final String PICKED_UP = "PICKED_UP";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";

    //fields
    private final int id;
    private final String pickupLabel; //"P1"
    private final Position pickup;
    private final String dropoffLabel; //"D2"
    private final Position dropoff;
    private String status;
    private Robot assignedRobot; //null until assigned
    private String failReason; //null unless FAILED

    //constructors, every task starts PENDING
    public Task(int id, String pickupLabel, Position pickup, String dropoffLabel, Position dropoff){
        this.id = id;
        this.pickupLabel = pickupLabel;
        this.pickup = pickup;
        this.dropoffLabel = dropoffLabel;
        this.dropoff = dropoff;
        this.status = PENDING;
        this.assignedRobot = null;
        this.failReason = null;
    }

    //for tests: labels the task by its coordinates
    public Task(int id, Position pickup, Position dropoff){
        this(id, pickup.toString(), pickup, dropoff.toString(), dropoff);
    }

    //status changes, markAssigned only allowed from PENDING
    public void markAssigned(Robot robot){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): markAssigned");
    }

    public void markPickedUp(){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): markPickedUp");
    }

    public void markCompleted(){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): markCompleted");
    }

    public void markFailed(String reason){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): markFailed");
    }

    //back to PENDING with no robot, e.g. its robot was stranded before picking up (Rule D)
    public void returnToPending(){
        //TODO (Role 6)
        throw new UnsupportedOperationException("TODO (Role 6): returnToPending");
    }

    //getters
    public int getId(){
        return id;
    }

    public String getPickupLabel(){
        return pickupLabel;
    }

    public Position getPickup(){
        return pickup;
    }

    public String getDropoffLabel(){
        return dropoffLabel;
    }

    public Position getDropoff(){
        return dropoff;
    }

    public String getStatus(){
        return status;
    }

    public Robot getAssignedRobot(){
        return assignedRobot;
    }

    public String getFailReason(){
        return failReason;
    }

    //prints as "#1 P1 -> D2 PENDING"
    @Override
    public String toString(){
        return "#" + id + " " + pickupLabel + " -> " + dropoffLabel + " " + status;
    }
}
