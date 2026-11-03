package warehouse;

//owner: Role 2 (warehouse map)
//one square of the warehouse: floor, obstacle, zone or charger
public class Cell{
    //cell types
    public static final char FLOOR = '.';
    public static final char OBSTACLE = '#';
    public static final char ZONE = 'Z'; //tasks pick up and drop off at zones
    public static final char CHARGER = 'C';

    //fields
    private final char type;
    private final String label; //"Z1", "C2" and so on, null for floor and obstacles

    //constructors, label defaults to null
    public Cell(char type){
        this(type, null);
    }

    public Cell(char type, String label){
        this.type = type;
        this.label = label;
    }

    //getters
    public char getType(){
        return type;
    }

    public String getLabel(){
        return label;
    }

    //everything except obstacles can be driven on
    public boolean isWalkable(){
        return type != OBSTACLE;
    }
}
