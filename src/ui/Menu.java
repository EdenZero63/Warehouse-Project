package ui;

import java.util.Scanner;

import exception.InvalidCommandException;
import exception.InvalidTaskException;
import exception.UnreachableDestinationException;
import sim.Simulation;
import task.Task;

//the menu: draw the screen, read a command, tell Simulation what to do, repeat
//no game rules in here, if something throws we catch it and print a normal message
//
//commands:
//  a       add a task          n [k]   advance 1 tick, or k ticks
//  r       reset (asks to confirm)     q       quit
public class Menu{
    //most ticks one n command can run
    public static final int MAX_STEPS = 100;
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
            throws InvalidCommandException, InvalidTaskException, UnreachableDestinationException{
        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        if(cmd.equals("")){
            //just hitting Enter doesn't do anything, the ticks run by themselves
        }else if(cmd.equals("a")){
            addTask();
        }else if(cmd.equals("n")){
            int ticks = 1;
            if(parts.length > 1){
                ticks = input.parseInt(parts[1], 1, MAX_STEPS);
            }
            for(int i = 0; i < ticks; i++){
                sim.nextTick();
            }
            message = "advanced " + ticks + (ticks == 1 ? " tick" : " ticks");
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
        sim.getTaskManager().checkPickup(pickup, sim); //no point asking for the drop-off if this one's wrong
        String dropoff = input.readLine("  drop-off point (e.g. D2): ").toUpperCase();
        Task task = sim.getTaskManager().createTask(pickup, dropoff, sim);
        message = "added task " + task;
    }

    //---------------- drawing ----------------

    //draws everything: banner, map + panel, legend, commands, message
    private void drawScreen(){
        String body = TextUtil.sideBySide(mapText(), panelText(), 3);
        int width = TextUtil.widestLine(body);

        //with ansi on we start back at the top and draw over the last screen so it doesn't scroll
        StringBuilder sb = new StringBuilder(Colors.isOn() ? Colors.CURSOR_HOME : "\n\n");
        sb.append(indent(banner(width)));
        sb.append(indent(body));
        //key on the left, commands on the right, splitting the width between them
        int keyWidth = (width - 3) / 2;
        int commandWidth = width - 3 - keyWidth;
        sb.append(indent(TextUtil.sideBySide(gridRenderer.renderLegend(keyWidth), commandsText(commandWidth), 3)));
        if(!message.equals("")){
            sb.append("  ").append(message).append("\n");
            message = "";
        }
        String screen = sb.toString();
        if(Colors.isOn()){
            //wipe whatever was left over from a longer line or an old message
            screen = screen.replace("\n", Colors.CLEAR_LINE_END + "\n") + Colors.CLEAR_BELOW;
        }
        System.out.print(screen);
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

    //the COMMANDS box, same height as the KEY box so they sit nicely next to each other
    private String commandsText(int width){
        String[][] rows = {
            {"a", "add a task, like P1 to D2"},
            {"n 5", "advance 5 ticks, just n is 1 tick"},
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
            + TextUtil.rule('=', width) + "\n";
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
