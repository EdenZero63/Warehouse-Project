package ui;

import java.util.ArrayList;

import exception.InvalidPositionException;
import robot.Robot;
import robot.StrandedState;
import sim.Simulation;
import warehouse.Cell;
import warehouse.Position;
import warehouse.Warehouse;

//draws the warehouse map as a boxed grid; obstacles that touch merge into one solid block
public class GridRenderer{
    //every cell is exactly this wide, so "R1*" and "(Z1)" line up under the column numbers
    public static final int CELL_WIDTH = 4;

    //the map as text, with row/column numbers
    //robots draw on top of cells; a carrying robot shows as "R1*", a stranded one as "R3!"
    public String render(Simulation sim){
        Warehouse w = sim.getWarehouse();
        int rows = w.getRows();
        int cols = w.getCols();
        StringBuilder sb = new StringBuilder();

        //column numbers
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
                //the wall between two obstacles is filled in
                sb.append(isObstacle(w, r, c - 1) && isObstacle(w, r, c) ? "#" : "|");
                if(c < cols){
                    sb.append(tileText(sim, r, c));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    //what each symbol means, printed under the screen
    public String renderLegend(){
        return "(Z1) zone   [C1] charger   #### obstacle   ..  planned route   R1* carrying   R1! stranded";
    }

    //+----+----+ line above row r; parts between obstacles are filled with #
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

    //exactly CELL_WIDTH chars for one cell
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
                text = label; //"Z10" is too long for brackets
            }
            return TextUtil.pad(text, CELL_WIDTH);
        }
        if(onPlannedPath(sim, p)){
            return " .. ";
        }
        return TextUtil.repeat(' ', CELL_WIDTH);
    }

    //"R1", "R1*" when carrying, "R1!" when stranded
    public static String robotSymbol(Robot robot){
        if(robot.getState() instanceof StrandedState){
            return robot.getId() + "!";
        }
        if(robot.isCarrying()){
            return robot.getId() + "*";
        }
        return robot.getId();
    }

    //null when no robot is there
    private Robot robotAt(Simulation sim, Position p){
        ArrayList<Robot> robots = sim.getRobots();
        for(int i = 0; i < robots.size(); i++){
            if(p.equals(robots.get(i).getPosition())){
                return robots.get(i);
            }
        }
        return null;
    }

    //true if any robot still has this cell on its path
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

    //null when off the grid
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

    //off the grid counts as not an obstacle so the outer border stays normal
    private boolean isObstacle(Warehouse w, int r, int c){
        Cell cell = cellAt(w, r, c);
        return cell != null && cell.getType() == Cell.OBSTACLE;
    }
}
