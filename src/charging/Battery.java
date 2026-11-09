
package charging;

//owner: Role 4 (charging)
//a robot's battery, always between 0 and ChargingPolicy.MAX_BATTERY
public class Battery {

    //fields
    private double level;

    //constructor
    public Battery(int startLevel) {
        //clamp startLevel into 0..MAX_BATTERY
        this.level = Math.max(0,
                Math.min(startLevel, ChargingPolicy.MAX_BATTERY));
    }

    //getters
    public double getLevel() {
        return level;
    }

    //use up power, never below 0
    public void consume(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Amount cannot be negative.");
        }

        level = Math.max(0, level - amount);
    }

    //add power, never above MAX_BATTERY
    public void charge(double amount) {
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

    //the level as text, whole numbers skip the .0 so it reads 55% or 97.5%
    public String getPercentText() {
        if (level == (int) level) {
            return (int) level + "%";
        }
        return level + "%";
    }

    //battery consumption: 1 tile moved costs MOVE_COST
    public void consumeTileMovement() {
        consume(ChargingPolicy.MOVE_COST);
    }
}
