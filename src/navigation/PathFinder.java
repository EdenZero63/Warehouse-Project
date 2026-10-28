package navigation;

import java.util.ArrayList;

import exception.UnreachableDestinationException;
import warehouse.Position;
import warehouse.Warehouse;

//owner: Role 5 (navigation)
//route-finding strategy; robots and charging rules only talk to this interface,
//so a smarter algorithm can replace BFS later without changing them
public interface PathFinder{

    //steps from `from` to `to`, not including `from`; never crosses obstacles or leaves the grid
    ArrayList<Position> findPath(Warehouse warehouse, Position from, Position to)
            throws UnreachableDestinationException;

    //same, but also treats `blocked` cells as walls (used to re-plan around other robots)
    ArrayList<Position> findPath(Warehouse warehouse, Position from, Position to, ArrayList<Position> blocked)
            throws UnreachableDestinationException;

    //number of steps on the path, every battery rule uses this distance
    int distance(Warehouse warehouse, Position from, Position to)
            throws UnreachableDestinationException;
}
