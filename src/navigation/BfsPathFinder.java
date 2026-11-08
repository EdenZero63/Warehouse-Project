package navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import exception.InvalidPositionException;
import exception.UnreachableDestinationException;
import warehouse.Cell;
import warehouse.Position;
import warehouse.Warehouse;

//owner: Role 5 (navigation)
//breadth-first search, always finds a shortest path on the grid
public class BfsPathFinder implements PathFinder{

    //no blocked cells
    @Override
    public ArrayList<Position> findPath(Warehouse warehouse, Position from, Position to)
            throws UnreachableDestinationException{
        return findPath(warehouse, from, to, new ArrayList<Position>());
    }

    //blocked cells are treated as walls
    @Override
    public ArrayList<Position> findPath(Warehouse warehouse, Position from, Position to, ArrayList<Position> blocked)
            throws UnreachableDestinationException{
        //already there, nothing to walk
        if(from.equals(to)){
            return new ArrayList<Position>();
        }

        //direction based on current position, going north south east west
        int[] dirRow = {-1, 1, 0, 0};
        int[] dirCol = {0, 0, 1, -1};

        //one queue of whole positions so a row can never get split from its column
        Queue<Position> toVisit = new LinkedList<Position>();
        //each cell we reached and the cell we came from, also tells us what's been visited
        HashMap<Position, Position> cameFrom = new HashMap<Position, Position>();
        toVisit.add(from);
        cameFrom.put(from, null);

        while(!toVisit.isEmpty()){
            Position current = toVisit.remove();

            //first time we reach the target is the shortest way there
            if(current.equals(to)){
                return buildPath(cameFrom, to);
            }

            //check the 4 cardinal directions
            for(int count = 0; count < 4; count++){
                Position next = current.move(dirRow[count], dirCol[count]);
                if(!cameFrom.containsKey(next) && canEnter(warehouse, next, to, blocked)){
                    cameFrom.put(next, current);
                    toVisit.add(next);
                }
            }
        }

        //ran out of cells without finding the target
        throw new UnreachableDestinationException("no path from " + from + " to " + to);
    }

    //distance is just the path length
    @Override
    public int distance(Warehouse warehouse, Position from, Position to)
            throws UnreachableDestinationException{
        return findPath(warehouse, from, to).size();
    }

    //walkable, not blocked, and only drive onto a charger if that charger is where we're going
    private boolean canEnter(Warehouse warehouse, Position cell, Position to, ArrayList<Position> blocked){
        if(!warehouse.isWalkable(cell) || blocked.contains(cell)){
            return false;
        }
        if(cell.equals(to)){
            return true;
        }
        try{
            return warehouse.getCell(cell).getType() != Cell.CHARGER;
        }catch(InvalidPositionException e){
            //can't happen, isWalkable already said it's on the map
            throw new IllegalStateException(e.getMessage());
        }
    }

    //walks backward from the target using cameFrom, adding each step at the front
    //stops before `from` so the path only holds the steps still to take
    private ArrayList<Position> buildPath(HashMap<Position, Position> cameFrom, Position to){
        ArrayList<Position> path = new ArrayList<Position>();
        Position step = to;
        while(cameFrom.get(step) != null){
            path.add(0, step);
            step = cameFrom.get(step);
        }
        return path;
    }
}
