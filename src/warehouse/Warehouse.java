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
        //fill it with floor so there's never a null in the grid
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                grid[r][c] = new Cell(Cell.FLOOR);
            }
        }
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
        return isInBounds(p.getRow(), p.getCol());
    }

    public boolean isInBounds(int row, int col){
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    //in bounds and not an obstacle
    public boolean isWalkable(Position p){
        return isWalkable(p.getRow(), p.getCol());
    }

    public boolean isWalkable(int row, int col){
        //check bounds first so we never read outside the array
        if(!isInBounds(row, col)){
            return false;
        }
        return grid[row][col].isWalkable();
    }

    //read and write one cell, throws if off the grid
    public Cell getCell(Position p) throws InvalidPositionException{
        if(!isInBounds(p)){
            throw new InvalidPositionException(p + " is outside the " + rows + "x" + cols + " map");
        }
        return grid[p.getRow()][p.getCol()];
    }

    public void setCell(Position p, Cell cell) throws InvalidPositionException{
        if(!isInBounds(p)){
            throw new InvalidPositionException(p + " is outside the " + rows + "x" + cols + " map");
        }
        grid[p.getRow()][p.getCol()] = cell;
    }

    //finds a labelled cell such as "Z1", throws if no cell has that label
    public Position findLabel(String label) throws InvalidPositionException{
        //go through every cell until one has the same label
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                String here = grid[r][c].getLabel();
                if(here != null && here.equals(label)){
                    return new Position(r, c);
                }
            }
        }
        throw new InvalidPositionException("there's no " + label + " on the map");
    }
}
