package ui;

import java.util.ArrayList;

import charging.ChargingPolicy;
import charging.ChargingStation;
import robot.Robot;
import sim.Simulation;
import task.Task;
import warehouse.Position;


//the text around the map: header, robot cards, task table, chargers, recent events
public class StatusPanel{
    //width of the panel next to the map
    public static final int PANEL_WIDTH = 50;
    //most task rows shown, newest last
    public static final int MAX_TASK_ROWS = 6;

    //header, e.g. "Tick 12 | Done [1] | Active [2] | Pending [0]"
    public String renderHeader(Simulation sim){
        ArrayList<Task> tasks = sim.getTaskManager().getAll();
        int done = 0;
        int active = 0;
        int pending = 0;
        for(int i = 0; i < tasks.size(); i++){
            String status = tasks.get(i).getStatus();
            if(status.equals(Task.COMPLETED)){
                done++;
            }else if(status.equals(Task.PENDING)){
                pending++;
            }else if(status.equals(Task.ASSIGNED) || status.equals(Task.PICKED_UP)){
                active++;
            }
        }
        return "Tick " + sim.getTick() + " | Done [" + done + "] | Active [" + active + "] | Pending [" + pending + "]";
    }

    //two lines per robot: ID, position, battery bar, percent / state, destination
    public String renderRobots(Simulation sim){
        StringBuilder sb = new StringBuilder();
        sb.append(sectionTitle("FLEET"));
        ArrayList<Robot> robots = sim.getRobots();
        for(int i = 0; i < robots.size(); i++){
            Robot r = robots.get(i);
            int level = r.getBattery().getLevel();
            String top = TextUtil.pad(GridRenderer.robotSymbol(r), 5)
                + TextUtil.pad(r.getPosition().toString(), 8)
                + batteryBar(level) + " " + level + "%";
            String bottom = "     " + TextUtil.pad(r.getState().getLabel(), 16) + destination(sim, r);
            sb.append(TextUtil.boxed(top, PANEL_WIDTH)).append("\n");
            sb.append(TextUtil.boxed(bottom, PANEL_WIDTH)).append("\n");
        }
        if(robots.isEmpty()){
            sb.append(TextUtil.boxed("no robots", PANEL_WIDTH)).append("\n");
        }
        return sb.toString();
    }

    //one row per task: ID, pickup -> drop-off, status, robot
    public String renderTasks(Simulation sim){
        StringBuilder sb = new StringBuilder();
        sb.append(sectionTitle("TASKS"));
        ArrayList<Task> tasks = sim.getTaskManager().getAll();
        if(tasks.isEmpty()){
            sb.append(TextUtil.boxed("none yet, press a to add one", PANEL_WIDTH)).append("\n");
        }
        int from = Math.max(0, tasks.size() - MAX_TASK_ROWS);
        for(int i = from; i < tasks.size(); i++){
            Task t = tasks.get(i);
            String robot = t.getAssignedRobot() == null ? "-" : t.getAssignedRobot().getId();
            String row = TextUtil.pad("#" + t.getId() + " " + t.getPickupLabel() + " -> " + t.getDropoffLabel(), 16)
                + TextUtil.pad(t.getStatus(), 11) + robot;
            sb.append(TextUtil.boxed(row, PANEL_WIDTH)).append("\n");
        }
        return sb.toString();
    }

    //one row per charger: who is on it and how many are waiting, then the battery numbers
    public String renderChargers(Simulation sim){
        StringBuilder sb = new StringBuilder();
        sb.append(sectionTitle("CHARGERS"));
        ArrayList<ChargingStation> chargers = sim.getChargers();
        for(int i = 0; i < chargers.size(); i++){
            ChargingStation cs = chargers.get(i);
            String status = cs.getOccupant() == null ? "free" : cs.getOccupant().getId() + " charging";
            if(cs.getQueueSize() > 0){
                status = status + ", " + cs.getQueueSize() + " waiting";
            }
            sb.append(TextUtil.boxed(TextUtil.pad(cs.getId(), 6) + status, PANEL_WIDTH)).append("\n");
        }
        String rates = "-" + ChargingPolicy.MOVE_COST + "%/move   +" + ChargingPolicy.CHARGE_RATE + "%/tick charging";
        sb.append(TextUtil.boxed(rates, PANEL_WIDTH)).append("\n");
        sb.append(TextUtil.rule('-', PANEL_WIDTH)).append("\n");
        return sb.toString();
    }

    //last `count` EventLog lines in a box `width` chars wide, newest marked with >>
    //always `count` rows tall so the screen doesn't jump around
    public String renderEvents(Simulation sim, int count, int width){
        StringBuilder sb = new StringBuilder();
        sb.append(TextUtil.rule('-', width)).append("\n");
        sb.append(TextUtil.boxed("EVENT LOG", width)).append("\n");
        sb.append(TextUtil.rule('-', width)).append("\n");
        ArrayList<String> recent = sim.getLog().getRecent(count);
        for(int i = 0; i < count; i++){
            String line = "";
            if(i < recent.size()){
                line = (i == recent.size() - 1 ? ">> " : "   ") + recent.get(i);
            }
            sb.append(TextUtil.boxed(line, width)).append("\n");
        }
        sb.append(TextUtil.rule('-', width)).append("\n");
        return sb.toString();
    }

    //battery bar like "[#########-]" on a 10-block scale
    public String batteryBar(int level){
        return TextUtil.bar(level, ChargingPolicy.MAX_BATTERY, 10);
    }

    //"#2 Z3 -> Z4", "-> C1" when heading to a charger, plus "(waiting 2)" when blocked
    private String destination(Simulation sim, Robot r){
        String text = "";
        if(r.getCurrentTask() != null){
            Task t = r.getCurrentTask();
            text = "#" + t.getId() + " " + t.getPickupLabel() + " -> " + t.getDropoffLabel();
        }else if(r.getPath() != null && !r.getPath().isEmpty()){
            Position end = r.getPath().get(r.getPath().size() - 1);
            text = "-> " + end;
            ArrayList<ChargingStation> chargers = sim.getChargers();
            for(int i = 0; i < chargers.size(); i++){
                if(end.equals(chargers.get(i).getPosition())){
                    text = "-> " + chargers.get(i).getId();
                }
            }
        }
        if(r.getWaitTicks() > 0){
            text = text + " (waiting " + r.getWaitTicks() + ")";
        }
        return text;
    }

    //+------+ / | NAME | / +------+
    private String sectionTitle(String name){
        return TextUtil.rule('-', PANEL_WIDTH) + "\n"
            + TextUtil.boxed(name, PANEL_WIDTH) + "\n"
            + TextUtil.rule('-', PANEL_WIDTH) + "\n";
    }
}
