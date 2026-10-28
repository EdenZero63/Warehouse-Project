package ui;

import sim.Simulation;

//owner: Role 7 (console UI)
//the text around the map: header, robot table, task table, recent events
public class StatusPanel{

    //header, e.g. "Tick 12 | Done [1] | Active [2] | Pending [0]"
    public String renderHeader(Simulation sim){
        //TODO (Role 7)
        throw new UnsupportedOperationException("TODO (Role 7): renderHeader");
    }

    //one row per robot: ID, battery bar, percent, state label, destination
    public String renderRobots(Simulation sim){
        //TODO (Role 7)
        throw new UnsupportedOperationException("TODO (Role 7): renderRobots");
    }

    //one row per task: ID, pickup -> drop-off, status, robot
    public String renderTasks(Simulation sim){
        //TODO (Role 7)
        throw new UnsupportedOperationException("TODO (Role 7): renderTasks");
    }

    //last `count` EventLog lines
    public String renderEvents(Simulation sim, int count){
        //TODO (Role 7)
        throw new UnsupportedOperationException("TODO (Role 7): renderEvents");
    }

    //battery bar like "[#########-]" on a 10-block scale
    public String batteryBar(int level){
        //TODO (Role 7)
        throw new UnsupportedOperationException("TODO (Role 7): batteryBar");
    }
}
