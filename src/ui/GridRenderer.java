package ui;

import java.util.ArrayList;

import exception.InvalidPositionException;
import robot.Robot;
import robot.StrandedState;
import sim.Simulation;
import warehouse.Cell;
import warehouse.Position;
import warehouse.Warehouse;

//draws the map as a grid of boxes, walls that touch join up into one big block
public class GridRenderer{
    //every cell is this wide so stuff like "R1*" and "(Z1)" lines up with the column numbers
    public static final int CELL_WIDTH = 4;

    //makes the whole map as text, with the row and column numbers
    //robots get drawn on top of the cell, "R1*" means it's carrying something and "R3!" means it's stuck
    public String render(Simulation sim){
        Warehouse w = sim.getWarehouse();
        int rows = w.getRows();
        int cols = w.getCols();
        StringBuilder sb = new StringBuilder();

        //column numbers on top
        sb.append("    ");
        for(int c = 0; c < cols; c++){
            sb.append(" ").append(TextUtil.pad(" " + c, CELL_WIDTH));
        }
        sb.append("\n");

        for(int r = 0; r <= rows; r++){
            sb.append(borderAbove(w, r)).append("\n");
            if(r == rows){
                break;
            }
            sb.append(TextUtil.pad("  " + r, 4));
            for(int c = 0; c <= cols; c++){
                //fill in the wall between 2 obstacles so they look connected
                sb.append(isObstacle(w, r, c - 1) && isObstacle(w, r, c) ? "#" : "|");
                if(c < cols){
                    sb.append(tileText(sim, r, c));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    //the legend at the bottom so you know what each symbol means
    public String renderLegend(){
        return "(Z1) zone   [C1] charger   #### obstacle   ..  planned route   R1* carrying   R1! stranded";
    }

    //the +----+----+ line above row r, if both sides are obstacles we fill it with #
    private String borderAbove(Warehouse w, int r){
        StringBuilder sb = new StringBuilder("    ");
        for(int c = 0; c <= w.getCols(); c++){
            boolean corner = isObstacle(w, r - 1, c - 1) && isObstacle(w, r - 1, c)
                && isObstacle(w, r, c - 1) && isObstacle(w, r, c);
            sb.append(corner ? "#" : "+");
            if(c < w.getCols()){
                sb.append(isObstacle(w, r - 1, c) && isObstacle(w, r, c) ? "####" : "----");
            }
        }
        return sb.toString();
    }

    //one cell, always CELL_WIDTH chars long
    private String tileText(Simulation sim, int r, int c){
        Position p = new Position(r, c);
        Robot robot = robotAt(sim, p);
        if(robot != null){
            return TextUtil.pad(" " + robotSymbol(robot), CELL_WIDTH);
        }

        Cell cell = cellAt(sim.getWarehouse(), r, c);
        if(cell == null){
            return " ?? ";
        }
        if(cell.getType() == Cell.OBSTACLE){
            return "####";
        }
        String label = cell.getLabel();
        if(label != null){
            String text = cell.getType() == Cell.CHARGER ? "[" + label + "]" : "(" + label + ")";
            if(text.length() > CELL_WIDTH){
                text = label; //"Z10" doesn't fit in the brackets
            }
            return TextUtil.pad(text, CELL_WIDTH);
        }
        if(onPlannedPath(sim, p)){
            return " .. ";
        }
        return TextUtil.repeat(' ', CELL_WIDTH);
    }

    //"R1" normally, "R1*" if it's carrying, "R1!" if it's stranded
    public static String robotSymbol(Robot robot){
        if(robot.getState() instanceof StrandedState){
            return robot.getId() + "!";
        }
        if(robot.isCarrying()){
            return robot.getId() + "*";
        }
        return robot.getId();
    }

    //null if there's no robot here
    private Robot robotAt(Simulation sim, Position p){
        ArrayList<Robot> robots = sim.getRobots();
        for(int i = 0; i < robots.size(); i++){
            if(p.equals(robots.get(i).getPosition())){
                return robots.get(i);
            }
        }
        return null;
    }

    //true if some robot is still going to pass through this cell
    private boolean onPlannedPath(Simulation sim, Position p){
        ArrayList<Robot> robots = sim.getRobots();
        for(int i = 0; i < robots.size(); i++){
            ArrayList<Position> path = robots.get(i).getPath();
            if(path != null && path.contains(p)){
                return true;
            }
        }
        return false;
    }

    //null if it's outside the grid
    private Cell cellAt(Warehouse w, int r, int c){
        if(r < 0 || r >= w.getRows() || c < 0 || c >= w.getCols()){
            return null;
        }
        try{
            return w.getCell(new Position(r, c));
        }catch(InvalidPositionException e){
            return null;
        }
    }

    //outside the grid doesn't count as an obstacle, otherwise the border looks weird
    private boolean isObstacle(Warehouse w, int r, int c){
        Cell cell = cellAt(w, r, c);
        return cell != null && cell.getType() == Cell.OBSTACLE;
    }
}
