
package charging;

//owner: Role 4 (charging)
//a robot's battery, always between 0 and ChargingPolicy.MAX_BATTERY
public class Battery {

    //fields
    private int level;

    //constructor
    public Battery(int startLevel) {
        //clamp startLevel into 0..MAX_BATTERY
        this.level = Math.max(0,
                Math.min(startLevel, ChargingPolicy.MAX_BATTERY));
    }

    //getters
    public int getLevel() {
        return level;
    }

    //use up power, never below 0
    public void consume(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Amount cannot be negative.");
        }

        level = Math.max(0, level - amount);
    }

    //add power, never above MAX_BATTERY
    public void charge(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Amount cannot be negative.");
        }

        level = Math.min(
                ChargingPolicy.MAX_BATTERY, level + amount);
    }

    //empty and full checks
    public boolean isEmpty() {
        return level == 0;
    }

    public boolean isFull() {
        return level == ChargingPolicy.MAX_BATTERY;
    }

    //battery consumption: 1 tile moved = 5%
    public void consumeTileMovement() {
        consume(5);
    }
}
