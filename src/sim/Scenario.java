package sim;

import java.util.ArrayList;

import charging.ChargingStation;
import exception.InvalidPositionException;
import robot.Robot;
import warehouse.Cell;
import warehouse.Position;
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
        int rows = layout.length;
        int cols = layout[0].length();
        Warehouse warehouse = new Warehouse(rows, cols);
        int zones = 0;
        int chargers = 0;

        //one Cell per character, Z and C get numbered as we go (reading order)
        for(int r = 0; r < rows; r++){
            if(layout[r].length() != cols){
                throw new IllegalArgumentException("layout row " + r + " should be " + cols + " characters long");
            }
            for(int c = 0; c < cols; c++){
                char type = layout[r].charAt(c);
                String label = null;
                if(type == Cell.ZONE){
                    zones++;
                    label = "Z" + zones;
                }else if(type == Cell.CHARGER){
                    chargers++;
                    label = "C" + chargers;
                }
                try{
                    warehouse.setCell(new Position(r, c), new Cell(type, label));
                }catch(InvalidPositionException e){
                    //can't happen, r and c are always inside the map we just made
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }
        return warehouse;
    }

    //fresh robots at their start positions
    public ArrayList<Robot> buildRobots(){
        ArrayList<Robot> robots = new ArrayList<Robot>();
        for(int i = 0; i < robotIds.length; i++){
            Position start = new Position(robotStarts[i][0], robotStarts[i][1]);
            robots.add(new Robot(robotIds[i], start, robotBatteries[i]));
        }
        return robots;
    }

    //one ChargingStation per 'C' in the layout: C1, C2, ...
    public ArrayList<ChargingStation> buildChargers(){
        ArrayList<ChargingStation> stations = new ArrayList<ChargingStation>();
        //same reading order as buildWarehouse so C1 here is the same C1 on the map
        for(int r = 0; r < layout.length; r++){
            for(int c = 0; c < layout[r].length(); c++){
                if(layout[r].charAt(c) == Cell.CHARGER){
                    String id = "C" + (stations.size() + 1);
                    stations.add(new ChargingStation(id, new Position(r, c)));
                }
            }
        }
        return stations;
    }
}
