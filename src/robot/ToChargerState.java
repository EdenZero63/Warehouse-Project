package robot;

import java.util.ArrayList;

import charging.ChargingStation;
import sim.Simulation;
import warehouse.Position;

//owner: Role 3 (robot behavior), rules from Role 4
//driving to a charger; the task (if any) is kept and resumed afterwards
public class ToChargerState extends RobotState{
    //fields
    private ChargingStation target; //null until the nearest one gets picked

    //constructors, no charger given means go to the nearest one
    public ToChargerState(){
        this.target = null;
    }

    public ToChargerState(ChargingStation target){
        this.target = target;
    }

    //step toward the charger, check in on arrival
    @Override
    public void act(Robot robot, Simulation sim){
        if(target == null){
            target = sim.getChargingPolicy().findNearestCharger(robot.getPosition(), sim);
            if(target == null){
                return; //no charger can be reached, stay put and try again next tick
            }
        }

        Position spot = target.getPosition();
        if(robot.getPosition().equals(spot)){
            checkIn(robot, sim);
            return;
        }
        if(robot.getBattery().isEmpty()){
            robot.strand(sim);
            return;
        }
        if(!robot.prepareRoute(spot, sim)){
            return;
        }

        //running low on the way to a far-off charger: settle for the nearest one, re-plans next tick
        if(sim.getChargingPolicy().mustGoCharge(robot, robot.getPath().get(0), sim)){
            ChargingStation nearest = sim.getChargingPolicy().findNearestCharger(robot.getPosition(), sim);
            if(nearest != null && nearest != target){
                target = nearest;
                robot.setPath(new ArrayList<Position>());
                return;
            }
        }

        //one step away and someone's charging: get in line right here, next to the charger (Rule C)
        if(robot.getPath().size() == 1 && !target.isFree()){
            robot.setPath(new ArrayList<Position>());
            checkIn(robot, sim);
            return;
        }

        robot.stepAlongPath(sim);
        if(robot.getPosition().equals(spot)){
            checkIn(robot, sim);
        }
    }

    //the station puts the robot in ChargingState or in line
    private void checkIn(Robot robot, Simulation sim){
        target.arrive(robot);
        if(target.getOccupant() == robot){
            sim.getLog().add(sim.getTick(), robot.getId() + " charging at " + target.getId());
        }else{
            sim.getLog().add(sim.getTick(), robot.getId() + " waiting for " + target.getId());
        }
    }

    @Override
    public String getLabel(){
        return "TO_CHARGER";
    }
}
