package ui;

import sim.Simulation;

//owner: Role 7 (console UI)
//draws the warehouse map
public class GridRenderer{
    //every cell is exactly this wide, so "R1*" and "." line up under the column numbers
    public static final int CELL_WIDTH = 4;

    //the map as text, with row/column numbers and the legend (including # and .)
    //robots draw on top of cells; a carrying robot shows as "R1*", a stranded one as "R3!"
    public String render(Simulation sim){
        //TODO (Role 7): use String.format("%-4s", label) for each cell
        throw new UnsupportedOperationException("TODO (Role 7): render");
    }
}
