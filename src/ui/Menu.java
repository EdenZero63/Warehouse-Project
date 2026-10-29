package ui;

import java.util.Scanner;

import exception.InvalidCommandException;
import exception.InvalidTaskException;
import exception.RobotUnavailableException;
import exception.UnreachableDestinationException;
import robot.Robot;
import sim.Simulation;
import task.Task;

//console menu: draw the screen, read one command, call Simulation, repeat
//no game rules in here; catch the domain exceptions and print a friendly message
//
//commands:
//  Enter   next tick           n <k>   run k ticks
//  a       add a task          i <n>   inspect robot n
//  c       service a stranded robot
//  r       reset (asks to confirm)     q       quit
public class Menu{
    //how many event log lines are shown
    public static final int EVENT_ROWS = 6;
    //most ticks one "n" command can run
    public static final int MAX_TICKS_PER_COMMAND = 100;

    //fields
    private final Simulation sim;
    private final InputHelper input;
    private final GridRenderer gridRenderer;
    private final StatusPanel statusPanel;
    private String message; //shown under the screen once, then cleared

    //constructor
    public Menu(Simulation sim){
        this.sim = sim;
        this.input = new InputHelper(new Scanner(System.in));
        this.gridRenderer = new GridRenderer();
        this.statusPanel = new StatusPanel();
        this.message = "";
    }

    //main loop; nothing in here may crash the program
    public void run(){
        try{
            sim.reset();
        }catch(UnsupportedOperationException e){
            message = notBuiltYet(e);
        }

        boolean running = true;
        while(running){
            drawScreen();
            String line = input.readLine("  > ");
            try{
                running = handle(line);
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

    //runs one command, returns false to quit
    private boolean handle(String line)
            throws InvalidCommandException, InvalidTaskException, UnreachableDestinationException, RobotUnavailableException{
        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        if(cmd.equals("")){
            sim.nextTick();
        }else if(cmd.equals("n")){
            if(parts.length < 2){
                throw new InvalidCommandException("use n <ticks>, e.g. n 5");
            }
            int ticks = input.parseInt(parts[1], 1, MAX_TICKS_PER_COMMAND);
            for(int i = 0; i < ticks; i++){
                sim.nextTick();
            }
            message = "ran " + ticks + " ticks";
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
        }else if(cmd.equals("q")){
            return false;
        }else{
            throw new InvalidCommandException("\"" + line + "\" isn't a command, see the list under the map");
        }
        return true;
    }

    //asks for two labels and lets TaskManager check them
    private void addTask() throws InvalidTaskException, UnreachableDestinationException{
        String pickup = input.readLine("  pickup label (e.g. Z1): ").toUpperCase();
        String dropoff = input.readLine("  drop-off label (e.g. Z4): ").toUpperCase();
        Task task = sim.getTaskManager().createTask(pickup, dropoff, sim);
        message = "added task " + task;
    }

    //one-line summary of a robot
    private void inspect(String text) throws RobotUnavailableException{
        Robot r = sim.getRobot(robotId(text));
        String task = r.getCurrentTask() == null ? "none" : r.getCurrentTask().toString();
        message = r + " | task " + task + " | " + r.getPath().size() + " steps left | blocked " + r.getWaitTicks() + " ticks";
    }

    //"2", "r2" and "R2" all mean "R2"
    private String robotId(String text){
        String id = text.trim().toUpperCase();
        if(!id.startsWith("R")){
            id = "R" + id;
        }
        return id;
    }

    //---------------- drawing ----------------

    //banner, map with the panel beside it, event log, legend, commands, last message
    private void drawScreen(){
        String body = TextUtil.sideBySide(mapText(), panelText(), 3);
        int width = TextUtil.widestLine(body);

        StringBuilder sb = new StringBuilder("\n\n");
        sb.append(indent(banner(width)));
        sb.append(indent(body)).append("\n");
        sb.append(indent(eventsText(width)));
        sb.append("   ").append(gridRenderer.renderLegend()).append("\n");
        sb.append("   [Enter] next tick   n <k> run k ticks   a add task   i <n> inspect   c service robot   r reset   q quit\n");
        if(!message.equals("")){
            sb.append("\n  ").append(message).append("\n");
            message = "";
        }
        System.out.print(sb);
    }

    //each part below is drawn on its own, so a part that isn't built yet doesn't hide the others

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

    //two spaces in front of every line, blank lines kept
    private String indent(String text){
        String[] lines = text.split("\n", -1);
        int count = lines.length;
        if(count > 0 && lines[count - 1].equals("")){
            count--; //text ends with \n, don't add an extra line
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < count; i++){
            sb.append("  ").append(lines[i]).append("\n");
        }
        return sb.toString();
    }

    //a teammate's method still throws its TODO; say which one instead of crashing
    private String notBuiltYet(UnsupportedOperationException e){
        return "Not built yet: " + e.getMessage();
    }
}
