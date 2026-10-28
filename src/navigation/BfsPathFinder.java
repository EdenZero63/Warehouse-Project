package navigation;

import java.util.ArrayList;

import exception.UnreachableDestinationException;
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
        //TODO (Role 5): BFS from `from`
        //  - queue of positions to visit (LinkedList or ArrayDeque)
        //  - HashMap<Position, Position> remembering where each cell was reached from
        //  - explore up/down/left/right, skip !warehouse.isWalkable(...) and anything in `blocked`
        //  - when `to` is reached, walk the map backwards to build the path
        //  - queue empties without reaching `to` -> throw UnreachableDestinationException
        throw new UnsupportedOperationException("TODO (Role 5): findPath");
    }

    //distance is just the path length
    @Override
    public int distance(Warehouse warehouse, Position from, Position to)
            throws UnreachableDestinationException{
        return findPath(warehouse, from, to).size();
    }
}
