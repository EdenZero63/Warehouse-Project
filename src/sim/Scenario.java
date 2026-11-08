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
    //P = pickup point, D = drop-off point, C = charger, # = rack, . = floor
    //P1 and P2 are on the left, P3 and P4 are in the middle aisle, D1 and D2 are on the right
    //P and D are numbered column by column (P1..P4, D1 D2), chargers in reading order (C1..C4)
    //routes only drive onto a charger if it's the destination
    //every P-to-D task fits in one battery at 5%/move from its closest charger (longest, like P3 -> D2, needs 86%)
    public static Scenario createDefault(){
        String[] layout = {
            "..........",
            ".##.##.##.",
            "C##P##.##C",
            ".P......D.",
            "....##....",
            "....##....",
            ".P......D.",
            "C##P##.##C",
            ".##.##.##.",
            "..........",
        };
        String[] ids = {"R1", "R2", "R3"};
        int[][] starts = {{3, 3}, {6, 6}, {9, 4}};
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
        int chargers = 0;

        //one Cell per character, C gets numbered as we go (reading order), P and D by column
        for(int r = 0; r < rows; r++){
            if(layout[r].length() != cols){
                throw new IllegalArgumentException("layout row " + r + " should be " + cols + " characters long");
            }
            for(int c = 0; c < cols; c++){
                char type = layout[r].charAt(c);
                String label = null;
                if(type == Cell.PICKUP || type == Cell.DROPOFF){
                    label = type + "" + numberByColumn(type, r, c);
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

    //which number this P or D gets: left column first, top to bottom inside a column
    private int numberByColumn(char type, int row, int col){
        int number = 0;
        for(int c = 0; c <= col; c++){
            for(int r = 0; r < layout.length; r++){
                if(layout[r].charAt(c) == type && (c < col || r <= row)){
                    number++;
                }
            }
        }
        return number;
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
