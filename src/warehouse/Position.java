package warehouse;

//owner: Role 2 (warehouse map)
//one (row, col) spot on the grid, can't change once made
public class Position{
    //fields
    private final int row;
    private final int col;

    //constructor
    public Position(int row, int col){
        this.row = row;
        this.col = col;
    }

    //getters
    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    //neighbouring position, e.g. move(-1, 0) is one step up
    public Position move(int dRow, int dCol){
        return new Position(row + dRow, col + dCol);
    }

    //equals and hashCode so Position works as a HashMap key
    @Override
    public boolean equals(Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Position)){
            return false;
        }
        Position p = (Position) other;
        return row == p.row && col == p.col;
    }

    @Override
    public int hashCode(){
        return row * 31 + col;
    }

    //prints as (row,col)
    @Override
    public String toString(){
        return "(" + row + "," + col + ")";
    }
}
