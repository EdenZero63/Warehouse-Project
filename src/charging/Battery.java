package charging;

//owner: Role 4 (charging)
//a robot's battery, always between 0 and ChargingPolicy.MAX_BATTERY
public class Battery{
    //fields
    private int level;

    //constructor
    public Battery(int startLevel){
        //TODO (Role 4): clamp startLevel into 0..MAX_BATTERY
        this.level = startLevel;
    }

    //getters
    public int getLevel(){
        return level;
    }

    //use up power, never below 0
    public void consume(int amount){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): consume");
    }

    //add power, never above MAX_BATTERY
    public void charge(int amount){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): charge");
    }

    //empty and full checks
    public boolean isEmpty(){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): isEmpty");
    }

    public boolean isFull(){
        //TODO (Role 4)
        throw new UnsupportedOperationException("TODO (Role 4): isFull");
    }
}
