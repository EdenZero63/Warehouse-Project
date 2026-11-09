package warehouse;

import exception.InvalidPositionException;
import sim.Scenario;
import testutil.Check;

//owner: Role 2 (warehouse map)
//tests for Warehouse, Cell and Position
public class WarehouseTest{
    public static void run(){
        Check.isTrue(new Position(1, 2).equals(new Position(1, 2)), "positions with the same row/col are equal");
        Check.isTrue(!new Cell(Cell.OBSTACLE).isWalkable(), "obstacle cells are not walkable");

        //empty map starts as all floor
        Warehouse empty = new Warehouse(3, 4);
        Check.isTrue(empty.isWalkable(2, 3), "new map is all floor");
        Check.isTrue(!empty.isInBounds(3, 0), "row 3 is off a 3-row map");

        //default 10x10 scenario
        Warehouse w = Scenario.createDefault().buildWarehouse();
        Check.equal(10, w.getRows(), "default map has 10 rows");
        Check.equal(10, w.getCols(), "default map has 10 cols");
        Check.isTrue(!w.isWalkable(-1, 0), "off the top is not walkable");
        Check.isTrue(!w.isWalkable(0, 10), "off the right side is not walkable");
        Check.isTrue(!w.isWalkable(1, 1), "racks are not walkable");
        Check.isTrue(w.isWalkable(3, 1), "pickup points are walkable");

        try{
            Check.equal(new Position(3, 1), w.findLabel("P1"), "P1 is at (3,1)");
            Check.equal(new Position(6, 8), w.findLabel("D2"), "D2 is at (6,8)");
            Check.equal(new Position(2, 0), w.findLabel("C1"), "C1 is at (2,0)");
            Check.equal(new Position(7, 9), w.findLabel("C4"), "C4 is at (7,9)");
            Check.equal(Cell.PICKUP, w.getCell(new Position(6, 1)).getType(), "(6,1) is a pickup point");
            Check.equal(Cell.DROPOFF, w.getCell(new Position(3, 8)).getType(), "(3,8) is a drop-off point");
        }catch(InvalidPositionException e){
            Check.isTrue(false, "labels should all be found: " + e.getMessage());
        }

        boolean threw = false;
        try{
            w.findLabel("P9");
        }catch(InvalidPositionException e){
            threw = true;
        }
        Check.isTrue(threw, "findLabel(\"P9\") throws");

        threw = false;
        try{
            w.getCell(new Position(10, 0));
        }catch(InvalidPositionException e){
            threw = true;
        }
        Check.isTrue(threw, "getCell off the grid throws");
    }
}
