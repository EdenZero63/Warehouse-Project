package ui;

import java.util.Scanner;

import exception.InvalidCommandException;
import exception.InvalidTaskException;
import exception.RobotUnavailableException;
import exception.UnreachableDestinationException;
import robot.Robot;
import sim.Simulation;
import task.Task;

//the menu: draw the screen, read a command, tell Simulation what to do, repeat
//no game rules in here, if something throws we catch it and print a normal message
//
//commands:
//  a       add a task          i <n>   inspect robot n
//  c       service a stranded robot
//  r       reset (asks to confirm)     q       quit
public class Menu{
    //how many lines of the event log to show
    public static final int EVENT_ROWS = 6;
    //how long each tick stays on screen when it's running by itself
    public static final int AUTO_TICK_MILLIS = 500;

    //fields
    private final Simulation sim;
    private final InputHelper input;
    private final GridRenderer gridRenderer;
    private final StatusPanel statusPanel;
    private String message; //message under the screen, only shows once

    //constructor
    public Menu(Simulation sim){
        this.sim = sim;
        this.input = new InputHelper(new Scanner(System.in));
        this.gridRenderer = new GridRenderer();
        this.statusPanel = new StatusPanel();
        this.message = "";
    }

    //main loop, this should never crash the program
    public void run(){
        try{
            sim.reset();
        }catch(UnsupportedOperationException e){
            message = notBuiltYet(e);
        }

        boolean running = true;
        while(running){
            drawScreen();
            try{
                if(shouldTickByItself()){
                    sim.nextTick();
                }else{
                    running = handle(input.readLine("  > "));
                }
            }catch(InvalidCommandException e){
                message = "Invalid command: " + e.getMessage();
            }catch(InvalidTaskException e){
                message = "Can't add that task: " + e.getMessage();
            }catch(UnreachableDestinationException e){
                message = "Can't get there: " + e.getMessage();
            }catch(RobotUnavailableException e){
                message = "Robot problem: " + e.getMessage();
            }catch(UnsupportedOperationException e){
                message = notBuiltYet(e);
            }
        }
        System.out.println("  Bye!");
    }

    //true if the robots have stuff to do and you didn't type anything,
    //that's what makes the ticks run by themselves. when they're done it just waits for you
    private boolean shouldTickByItself(){
        if(!sim.hasWork()){
            return false;
        }
        System.out.println("  running by itself, type a command and hit Enter anytime");
        pause();
        return !input.hasLineWaiting();
    }

    //holds the screen for a bit so you can actually watch them move
    private void pause(){
        try{
            Thread.sleep(AUTO_TICK_MILLIS);
        }catch(InterruptedException e){
            //nobody interrupts this, and if they did the next tick just shows up early
        }
    }

    //does one command, returns false when you quit
    private boolean handle(String line)
            throws InvalidCommandException, InvalidTaskException, UnreachableDestinationException, RobotUnavailableException{
        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        if(cmd.equals("")){
            //just hitting Enter doesn't do anything, the ticks run by themselves
        }else if(cmd.equals("a")){
            addTask();
        }else if(cmd.equals("i")){
            if(parts.length < 2){
                throw new InvalidCommandException("use i <robot>, e.g. i 2");
            }
            inspect(parts[1]);
        }else if(cmd.equals("c")){
            String id = robotId(input.readLine("  robot to service (e.g. R2): "));
            sim.serviceRobot(id);
            message = id + " serviced";
        }else if(cmd.equals("r")){
            String answer = input.readLine("  reset everything back to tick 0? (y/n): ");
            if(answer.equalsIgnoreCase("y")){
                sim.reset();
                message = "reset to tick 0";
            }else{
                message = "reset cancelled";
            }
        }else if(cmd.equals("k")){
            Colors.setOn(!Colors.isOn());
            if(Colors.isOn()){
                message = "colors on";
            }else{
                message = "colors off";
            }
        }else if(cmd.equals("q")){
            return false;
        }else{
            throw new InvalidCommandException("\"" + line + "\" isn't a command, see the list under the map");
        }
        return true;
    }

    //asks for pickup and drop-off and lets TaskManager check if they're ok
    private void addTask() throws InvalidTaskException, UnreachableDestinationException{
        String pickup = input.readLine("  pickup point (e.g. P1): ").toUpperCase();
        String dropoff = input.readLine("  drop-off point (e.g. D2): ").toUpperCase();
        Task task = sim.getTaskManager().createTask(pickup, dropoff, sim);
        message = "added task " + task;
    }

    //short summary of one robot
    private void inspect(String text) throws RobotUnavailableException{
        Robot r = sim.getRobot(robotId(text));
        String task = r.getCurrentTask() == null ? "none" : r.getCurrentTask().toString();
        message = r + " | task " + task + " | " + r.getPath().size() + " steps left | blocked " + r.getWaitTicks() + " ticks";
    }

    //so "2", "r2" and "R2" all work
    private String robotId(String text){
        String id = text.trim().toUpperCase();
        if(!id.startsWith("R")){
            id = "R" + id;
        }
        return id;
    }

    //---------------- drawing ----------------

    //draws everything: banner, map + panel, events, legend, commands, message
    private void drawScreen(){
        String body = TextUtil.sideBySide(mapText(), panelText(), 3);
        int width = TextUtil.widestLine(body);

        StringBuilder sb = new StringBuilder("\n\n");
        sb.append(indent(banner(width)));
        sb.append(indent(body)).append("\n");
        sb.append(indent(eventsText(width))).append("\n");
        //key on the left, commands on the right, splitting the width between them
        int keyWidth = (width - 3) / 2;
        int commandWidth = width - 3 - keyWidth;
        sb.append(indent(TextUtil.sideBySide(gridRenderer.renderLegend(keyWidth), commandsText(commandWidth), 3)));
        if(!message.equals("")){
            sb.append("\n  ").append(message).append("\n");
            message = "";
        }
        System.out.print(sb);
    }

    //each part gets drawn on its own so if one isn't done yet the rest still shows up

    private String mapText(){
        if(sim.getWarehouse() == null){
            return "(no map yet: needs Simulation.reset and Scenario.buildWarehouse)";
        }
        try{
            return gridRenderer.render(sim);
        }catch(UnsupportedOperationException e){
            return "(map not ready: " + e.getMessage() + ")";
        }
    }

    private String panelText(){
        try{
            return statusPanel.renderRobots(sim) + statusPanel.renderTasks(sim) + statusPanel.renderChargers(sim);
        }catch(UnsupportedOperationException e){
            return "(panel not ready: " + e.getMessage() + ")";
        }
    }

    private String eventsText(int width){
        try{
            return statusPanel.renderEvents(sim, EVENT_ROWS, width);
        }catch(UnsupportedOperationException e){
            return "(event log not ready: " + e.getMessage() + ")\n";
        }
    }

    //the COMMANDS box, same height as the KEY box so they sit nicely next to each other
    private String commandsText(int width){
        String[][] rows = {
            {"a", "add a task, like P1 to D2"},
            {"i 2", "inspect robot R2"},
            {"c", "service a stranded robot"},
            {"r", "reset back to tick 0"},
            {"k", "colors on/off"},
            {"q", "quit"},
        };
        return TextUtil.table("COMMANDS", rows, 8, width);
    }

    //title on the left, tick and task counts on the right
    private String banner(int width){
        String title = "W A R E H O U S E   R O B O T   F L E E T";
        String header;
        try{
            header = statusPanel.renderHeader(sim);
        }catch(UnsupportedOperationException e){
            header = "Tick " + sim.getTick();
        }
        int gap = Math.max(1, width - 4 - title.length() - header.length());
        return TextUtil.rule('=', width) + "\n"
            + TextUtil.boxed(title + TextUtil.repeat(' ', gap) + header, width) + "\n"
            + TextUtil.rule('=', width) + "\n\n";
    }

    //puts 2 spaces in front of every line (keeps the blank lines too)
    private String indent(String text){
        String[] lines = text.split("\n", -1);
        int count = lines.length;
        if(count > 0 && lines[count - 1].equals("")){
            count--; //text already ends with a newline so don't add another line
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++){
            sb.append("  ").append(lines[i]).append("\n");
        }
        return sb.toString();
    }

    //if someone's method still throws its TODO, say which one instead of crashing
    private String notBuiltYet(UnsupportedOperationException e){
        return "Not built yet: " + e.getMessage();
    }
}
