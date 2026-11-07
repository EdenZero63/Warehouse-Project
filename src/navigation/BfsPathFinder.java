package navigation;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

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
                try{
              ArrayList<Position> pathToTake= new ArrayList<Position>();

                //variables to track scope
                int maxRow= 9; //the max row size of the map
                int maxCol = 9; // max column size of the map
                int[][] matrix = new int[maxRow][maxCol]; //matrix of each position on the map
                int currentRow =0;//current row 
                int currentCol =0;// current column

                //queue data sctructure that represent the row and columns being added or popped from the list.
                Queue<Integer> rowQueue = new PriorityQueue<>();
                Queue<Integer> colQueue = new PriorityQueue<>();
                rowQueue.add(from.getRow());
                colQueue.add(from.getCol());

                Boolean [][] visited = new Boolean[maxRow][maxCol]; //boolean matrix to mark wether a vistied cell is true
                boolean reachedTo = false;
                //variables used to track the number of steps taken
                int moveCount = 0; // count for how many moves taken
                int nodes_left_in_layer =1; 
                int nodes_in_next_layer =0;

                //direction based current position and movement going north south east west 
                int[] dirRow= {-1,1,0,0}; 
                int[] dirCol={0,0,1,-1};

                
                pathToTake.add(new Position(from.getRow(), from.getCol()));
                visited[from.getRow()][from.getCol()] = true;

               while(rowQueue.size()> 0 || colQueue.size() >0)
               {
                    // transfer queues' position to current row/column variables
                    currentRow = rowQueue.poll();
                    currentCol = colQueue.poll();

                    //check if current matrix position is the position of the target.
                    if(matrix[currentRow][currentCol] == matrix[to.getRow()][to.getCol()])
                    {
                        reachedTo = true;
                        break;
                    }
                    //for loop for checking the 4 cardinal directions
                    for(int count =0; count< 4; count++)
                    {
                       int searchRow = currentRow + dirRow[count];
                       int searchCol = currentCol + dirCol[count];
                       
                       if(searchRow > 0 || searchCol >0) //if searched position greater than 0 keep searching
                       {
                        if(searchRow <= maxRow || searchCol  <= maxCol) //if searched position is less than max position keep searching
                        {
                            if(visited[searchRow][searchCol] == false) //if search row and column hasn't been visited continue 
                            {
                                if(warehouse.isWalkable(searchRow,searchCol)){
                                rowQueue.add(searchRow);
                                colQueue.add(searchCol);
                                visited[searchRow][searchCol] = true;
                                nodes_in_next_layer++;
                                }
                            }
                        }
                       }

                    } //end of for loop could be made into own function
                    pathToTake.add(new Position(currentRow, currentCol));

                    nodes_left_in_layer--;
                    if(nodes_left_in_layer == 0)
                    {
                        nodes_left_in_layer = nodes_in_next_layer;
                        nodes_in_next_layer =0;
                        moveCount++;
                    }
               }
              
               {
                
               }
               if(reachedTo == true)
               {
                    return pathToTake;//not entirely sure what to return here.
               }

        return findPath(warehouse, from, to, new ArrayList<Position>());
        
        }catch(UnreachableDestinationException e){
             throw new UnsupportedOperationException("Position "+ e + "is currently unrechable");
        }
       
    }

    //distance is just the path length
    @Override
    public int distance(Warehouse warehouse, Position from, Position to)
            throws UnreachableDestinationException{
        return findPath(warehouse, from, to).size();
    }
}
