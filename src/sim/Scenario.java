package sim;

import java.util.ArrayList;

import charging.ChargingStation;
import robot.Robot;
import warehouse.Warehouse;

//owner: Role 1 (integration lead), map building with Role 2
//a predefined starting setup: the map plus where each robot starts
//reset rebuilds everything from this, so a Scenario never changes while running
public class Scenario{
    //fields
    private final String name;
    private final String[] layout;
    private final String[] robotIds;
    private final int[][] robotStarts; //{row, col} per robot
    private final int[] robotBatteries;

    //constructor
    public Scenario(String name, String[] layout, String[] robotIds, int[][] robotStarts, int[] robotBatteries){
        this.name = name;
        this.layout = layout;
        this.robotIds = robotIds;
        this.robotStarts = robotStarts;
        this.robotBatteries = robotBatteries;
    }

    //10x10 warehouse: 2x2 racks with aisles between them
    //Z = zone (a task can pick up or drop off at any zone), C = charger, # = rack, . = floor
    //labels are numbered in reading order (left to right, top to bottom): Z1..Z4, C1..C4
    //each charger sits in a dead-end alcove 2 moves from its zone, so no route has to drive through one,
    //and every zone-to-zone task fits in one battery at 5%/move (longest needs 76%)
    public static Scenario createDefault(){
        String[] layout = {
            "..........",
            ".##.##.##.",
            "C##.##.##C",
            ".Z......Z.",
            "....##....",
            "....##....",
            ".Z......Z.",
            "C##.##.##C",
            ".##.##.##.",
            "..........",
        };
        String[] ids = {"R1", "R2", "R3"};
        int[][] starts = {{3, 2}, {6, 7}, {9, 4}};
        int[] batteries = {100, 90, 45}; //R3 starts low so charging shows up early in the demo
        return new Scenario("Default", layout, ids, starts, batteries);
    }

    //getters
    public String getName(){
        return name;
    }

    //fresh Warehouse built from the layout strings
    public Warehouse buildWarehouse(){
        //TODO (Role 2): new Warehouse(layout.length, layout[0].length()), then one Cell per character,
        //numbering Z and C labels in reading order (Z1, Z2, ... and C1, C2, ...)
        throw new UnsupportedOperationException("TODO (Role 2): buildWarehouse");
    }

    //fresh robots at their start positions
    public ArrayList<Robot> buildRobots(){
        //TODO (Role 1)
        throw new UnsupportedOperationException("TODO (Role 1): buildRobots");
    }

    //one ChargingStation per 'C' in the layout: C1, C2, ...
    public ArrayList<ChargingStation> buildChargers(){
        //TODO (Role 1)
        throw new UnsupportedOperationException("TODO (Role 1): buildChargers");
    }
}
