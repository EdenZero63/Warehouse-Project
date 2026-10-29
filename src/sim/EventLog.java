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
        entries.add("T" + tick + " " + message);
    }

    //last `count` entries, oldest first
    public ArrayList<String> getRecent(int count){
        ArrayList<String> recent = new ArrayList<String>();
        //start count entries from the end (or at 0 if there aren't that many yet)
        int start = Math.max(0, entries.size() - count);
        for(int i = start; i < entries.size(); i++){
            recent.add(entries.get(i));
        }
        return recent;
    }

    //used by reset
    public void clear(){
        entries.clear();
    }
}
