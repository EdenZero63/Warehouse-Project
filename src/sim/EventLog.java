package sim;

import java.util.ArrayList;

//owner: Role 1 (integration lead)
//short history lines like "T12 R2 picked up at P2", shown under the map
public class EventLog{
    //fields
    private final ArrayList<String> entries;

    //constructor
    public EventLog(){
        this.entries = new ArrayList<String>();
    }

    //add one line, stored as "T12 message"
    public void add(int tick, String message){
        //TODO (Role 1)
        throw new UnsupportedOperationException("TODO (Role 1): add");
    }

    //last `count` entries, oldest first
    public ArrayList<String> getRecent(int count){
        //TODO (Role 1)
        throw new UnsupportedOperationException("TODO (Role 1): getRecent");
    }

    //used by reset
    public void clear(){
        entries.clear();
    }
}
