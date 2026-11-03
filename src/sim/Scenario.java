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

    //the map from the team prototype, plus a second charger (C2) as recommended in DESIGN.md Rule C
    //P = pickup, D = drop-off, C = charger, # = obstacle, . = floor
    //labels are numbered in reading order (left to right, top to bottom): P1, P2, D1, D2, C1, C2
    public static Scenario createDefault(){
        String[] layout = {
            "P..#.....D",
            "...#.##...",
            ".....#....",
            "##..C..#..",
            "...##..#..",
            "P........D",
            ".##..#....",
            "........C.",
        };
        String[] ids = {"R1", "R2", "R3"};
        int[][] starts = {{1, 1}, {2, 7}, {5, 6}};
        int[] batteries = {100, 60, 25}; //R3 starts low so charging shows up early in the demo
        return new Scenario("Default", layout, ids, starts, batteries);
    }

    //getters
    public String getName(){
        return name;
    }

    //fresh Warehouse built from the layout strings
    public Warehouse buildWarehouse(){
        //TODO (Role 2): new Warehouse(layout.length, layout[0].length()), then one Cell per character,
        //numbering P/D/C labels in reading order
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
