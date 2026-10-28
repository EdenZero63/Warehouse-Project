package warehouse;

import exception.InvalidPositionException;

//owner: Role 2 (warehouse map)
//the grid; knows what is at each position, knows nothing about robots
public class Warehouse{
    //fields
    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    //constructor, every cell starts as floor
    public Warehouse(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        //TODO (Role 2): fill every cell with a FLOOR cell so the grid never holds null
    }

    //getters
    public int getRows(){
        return rows;
    }

    public int getCols(){
        return cols;
    }

    //bounds checks, by Position or by row/col
    public boolean isInBounds(Position p){
        //TODO (Role 2)
        throw new UnsupportedOperationException("TODO (Role 2): isInBounds");
    }

    public boolean isInBounds(int row, int col){
        //TODO (Role 2)
        throw new UnsupportedOperationException("TODO (Role 2): isInBounds");
    }

    //in bounds and not an obstacle
    public boolean isWalkable(Position p){
        //TODO (Role 2)
        throw new UnsupportedOperationException("TODO (Role 2): isWalkable");
    }

    public boolean isWalkable(int row, int col){
        //TODO (Role 2)
        throw new UnsupportedOperationException("TODO (Role 2): isWalkable");
    }

    //read and write one cell, throws if off the grid
    public Cell getCell(Position p) throws InvalidPositionException{
        //TODO (Role 2)
        throw new UnsupportedOperationException("TODO (Role 2): getCell");
    }

    public void setCell(Position p, Cell cell) throws InvalidPositionException{
        //TODO (Role 2)
        throw new UnsupportedOperationException("TODO (Role 2): setCell");
    }

    //finds a labelled cell such as "P1", throws if no cell has that label
    public Position findLabel(String label) throws InvalidPositionException{
        //TODO (Role 2)
        throw new UnsupportedOperationException("TODO (Role 2): findLabel");
    }
}
